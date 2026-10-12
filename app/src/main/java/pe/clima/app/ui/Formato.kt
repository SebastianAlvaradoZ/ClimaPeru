package pe.clima.app.ui

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

private val ES_PE: Locale = Locale.forLanguageTag("es-PE")

fun grados(valor: Double): String = if (valor.isNaN()) "–" else "${valor.roundToInt()}°"

fun nombreDia(fecha: LocalDate): String {
    val hoy = LocalDate.now()
    return when (fecha) {
        hoy -> "Hoy"
        hoy.plusDays(1) -> "Mañana"
        else -> fecha.dayOfWeek.getDisplayName(TextStyle.FULL, ES_PE)
            .replaceFirstChar { it.titlecase(ES_PE) } + " ${fecha.dayOfMonth}"
    }
}
