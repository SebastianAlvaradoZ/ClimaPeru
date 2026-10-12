package pe.clima.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.clima.app.ClimaCiudad
import pe.clima.app.PronosticoDia
import pe.clima.app.descripcionClima
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(clima: ClimaCiudad, onVolver: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(clima.ciudad.nombre) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { ClimaActualCard(clima) }
            item {
                Text(
                    "Pronóstico de 7 días",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            item {
                Card {
                    Column {
                        clima.pronostico.forEachIndexed { i, dia ->
                            if (i > 0) HorizontalDivider()
                            FilaPronostico(dia)
                        }
                    }
                }
            }
            item {
                Text(
                    "Datos: Open-Meteo.com",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ClimaActualCard(clima: ClimaCiudad) {
    val a = clima.actual
    val (texto, emoji) = descripcionClima(a.codigo, a.esDeDia)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(clima.ciudad.departamento, style = MaterialTheme.typography.labelLarge)
            Text(emoji, fontSize = 64.sp)
            Text(grados(a.temperatura), fontSize = 56.sp, fontWeight = FontWeight.Bold)
            Text(texto, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Dato("Sensación", grados(a.sensacion))
                Dato("Humedad", "${a.humedad}%")
                Dato("Viento", "${a.viento.roundToInt()} km/h")
            }
        }
    }
}

@Composable
private fun Dato(etiqueta: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(etiqueta, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun FilaPronostico(dia: PronosticoDia) {
    val (texto, emoji) = descripcionClima(dia.codigo)
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(nombreDia(dia.fecha), fontWeight = FontWeight.Medium)
            Text(texto, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(emoji, fontSize = 24.sp)
        Spacer(Modifier.width(12.dp))
        Text(
            dia.probLluvia?.let { "💧$it%" } ?: "",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(52.dp),
        )
        Text(
            "${grados(dia.maxima)} / ${grados(dia.minima)}",
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(72.dp),
        )
    }
}
