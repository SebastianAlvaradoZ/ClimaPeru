package pe.clima.app

/** Capital de cada departamento del Perú (más Callao) con sus coordenadas. */
data class Ciudad(
    val nombre: String,
    val departamento: String,
    val latitud: Double,
    val longitud: Double,
)

val CIUDADES_PERU = listOf(
    Ciudad("Chachapoyas", "Amazonas", -6.2317, -77.8690),
    Ciudad("Huaraz", "Áncash", -9.5278, -77.5278),
    Ciudad("Abancay", "Apurímac", -13.6339, -72.8814),
    Ciudad("Arequipa", "Arequipa", -16.4090, -71.5375),
    Ciudad("Ayacucho", "Ayacucho", -13.1588, -74.2239),
    Ciudad("Cajamarca", "Cajamarca", -7.1617, -78.5128),
    Ciudad("Callao", "Callao", -12.0566, -77.1181),
    Ciudad("Cusco", "Cusco", -13.5319, -71.9675),
    Ciudad("Huancavelica", "Huancavelica", -12.7861, -74.9764),
    Ciudad("Huánuco", "Huánuco", -9.9306, -76.2422),
    Ciudad("Ica", "Ica", -14.0678, -75.7286),
    Ciudad("Huancayo", "Junín", -12.0651, -75.2049),
    Ciudad("Trujillo", "La Libertad", -8.1116, -79.0288),
    Ciudad("Chiclayo", "Lambayeque", -6.7714, -79.8409),
    Ciudad("Lima", "Lima", -12.0464, -77.0428),
    Ciudad("Iquitos", "Loreto", -3.7491, -73.2538),
    Ciudad("Puerto Maldonado", "Madre de Dios", -12.5933, -69.1891),
    Ciudad("Moquegua", "Moquegua", -17.1983, -70.9357),
    Ciudad("Cerro de Pasco", "Pasco", -10.6864, -76.2625),
    Ciudad("Piura", "Piura", -5.1945, -80.6328),
    Ciudad("Puno", "Puno", -15.8402, -70.0219),
    Ciudad("Moyobamba", "San Martín", -6.0342, -76.9717),
    Ciudad("Tacna", "Tacna", -18.0066, -70.2463),
    Ciudad("Tumbes", "Tumbes", -3.5669, -80.4515),
    Ciudad("Pucallpa", "Ucayali", -8.3791, -74.5539),
)
