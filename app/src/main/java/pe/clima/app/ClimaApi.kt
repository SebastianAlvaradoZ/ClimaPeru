package pe.clima.app

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate

data class ClimaActual(
    val temperatura: Double,
    val sensacion: Double,
    val humedad: Int,
    val viento: Double,
    val codigo: Int,
    val esDeDia: Boolean,
)

data class PronosticoDia(
    val fecha: LocalDate,
    val codigo: Int,
    val maxima: Double,
    val minima: Double,
    val probLluvia: Int?,
)

data class ClimaCiudad(
    val ciudad: Ciudad,
    val actual: ClimaActual,
    val pronostico: List<PronosticoDia>,
)

/** Pide el clima de todas las ciudades en una sola llamada con Retrofit. */
object ClimaApi {

    private val servicio: OpenMeteoService = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenMeteoService::class.java)

    suspend fun obtenerClima(ciudades: List<Ciudad>): List<ClimaCiudad> {
        val respuestas = servicio.pronostico(
            latitudes = ciudades.joinToString(",") { it.latitud.toString() },
            longitudes = ciudades.joinToString(",") { it.longitud.toString() },
        )
        return ciudades.zip(respuestas) { ciudad, dto -> dto.aDominio(ciudad) }
    }

    private fun RespuestaDto.aDominio(ciudad: Ciudad): ClimaCiudad {
        val c = current ?: error("Respuesta sin clima actual para ${ciudad.nombre}")
        val actual = ClimaActual(
            temperatura = c.temperatura ?: Double.NaN,
            sensacion = c.sensacion ?: Double.NaN,
            humedad = c.humedad ?: 0,
            viento = c.viento ?: 0.0,
            codigo = c.codigo ?: -1,
            esDeDia = c.esDeDia != 0,
        )
        val d = daily
        val pronostico = d?.fechas.orEmpty().mapIndexed { i, fecha ->
            PronosticoDia(
                fecha = LocalDate.parse(fecha),
                codigo = d?.codigos?.getOrNull(i) ?: -1,
                maxima = d?.maximas?.getOrNull(i) ?: Double.NaN,
                minima = d?.minimas?.getOrNull(i) ?: Double.NaN,
                probLluvia = d?.probLluvia?.getOrNull(i),
            )
        }
        return ClimaCiudad(ciudad, actual, pronostico)
    }
}

/** Traduce el código WMO que usa Open-Meteo a un texto y un emoji. */
fun descripcionClima(codigo: Int, esDeDia: Boolean = true): Pair<String, String> = when (codigo) {
    0 -> "Despejado" to if (esDeDia) "☀️" else "🌙"
    1 -> "Mayormente despejado" to if (esDeDia) "🌤️" else "🌙"
    2 -> "Parcialmente nublado" to "⛅"
    3 -> "Nublado" to "☁️"
    45, 48 -> "Neblina" to "🌫️"
    51, 53, 55 -> "Llovizna" to "🌦️"
    56, 57 -> "Llovizna helada" to "🌧️"
    61, 63, 65 -> "Lluvia" to "🌧️"
    66, 67 -> "Lluvia helada" to "🌧️"
    71, 73, 75, 77 -> "Nieve" to "❄️"
    80, 81, 82 -> "Chubascos" to "🌦️"
    85, 86 -> "Chubascos de nieve" to "🌨️"
    95 -> "Tormenta" to "⛈️"
    96, 99 -> "Tormenta con granizo" to "⛈️"
    else -> "Desconocido" to "🌡️"
}
