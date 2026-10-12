package pe.clima.app

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

/** Endpoint de pronóstico de Open-Meteo (https://open-meteo.com). Gratis y sin API key. */
interface OpenMeteoService {

    /**
     * Con varias coordenadas separadas por comas la API devuelve un arreglo,
     * una respuesta por ubicación y en el mismo orden.
     */
    @GET("v1/forecast")
    suspend fun pronostico(
        @Query("latitude") latitudes: String,
        @Query("longitude") longitudes: String,
        @Query("current") actual: String =
            "temperature_2m,apparent_temperature,relative_humidity_2m,wind_speed_10m,weather_code,is_day",
        @Query("daily") diario: String =
            "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max",
        @Query("timezone") zonaHoraria: String = "America/Lima",
        @Query("forecast_days") dias: Int = 7,
    ): List<RespuestaDto>
}

// Gson ignora los valores por defecto de Kotlin, por eso todos los campos son nulables.

data class RespuestaDto(
    val current: ActualDto?,
    val daily: DiarioDto?,
)

data class ActualDto(
    @SerializedName("temperature_2m") val temperatura: Double?,
    @SerializedName("apparent_temperature") val sensacion: Double?,
    @SerializedName("relative_humidity_2m") val humedad: Int?,
    @SerializedName("wind_speed_10m") val viento: Double?,
    @SerializedName("weather_code") val codigo: Int?,
    @SerializedName("is_day") val esDeDia: Int?,
)

data class DiarioDto(
    @SerializedName("time") val fechas: List<String>?,
    @SerializedName("weather_code") val codigos: List<Int?>?,
    @SerializedName("temperature_2m_max") val maximas: List<Double?>?,
    @SerializedName("temperature_2m_min") val minimas: List<Double?>?,
    @SerializedName("precipitation_probability_max") val probLluvia: List<Int?>?,
)
