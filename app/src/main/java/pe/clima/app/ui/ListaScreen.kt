package pe.clima.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.clima.app.ClimaCiudad
import pe.clima.app.EstadoClima
import pe.clima.app.descripcionClima
import java.text.Normalizer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    estado: EstadoClima,
    onRecargar: () -> Unit,
    onSeleccionar: (ClimaCiudad) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clima en el Perú 🇵🇪") },
                actions = {
                    IconButton(onClick = onRecargar) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
        },
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (estado) {
                EstadoClima.Cargando -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is EstadoClima.Error -> ErrorMensaje(estado.mensaje, onRecargar, Modifier.align(Alignment.Center))
                is EstadoClima.Listo -> ListaCiudades(estado.datos, onSeleccionar)
            }
        }
    }
}

@Composable
private fun ListaCiudades(datos: List<ClimaCiudad>, onSeleccionar: (ClimaCiudad) -> Unit) {
    var busqueda by rememberSaveable { mutableStateOf("") }
    val filtradas = datos.filter {
        val q = sinTildes(busqueda)
        q.isBlank() || sinTildes(it.ciudad.nombre).contains(q) || sinTildes(it.ciudad.departamento).contains(q)
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item { Resumen(datos) }
        item {
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar ciudad o departamento") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
            )
        }
        items(filtradas, key = { it.ciudad.nombre }) { clima ->
            TarjetaCiudad(clima, onClick = { onSeleccionar(clima) })
        }
        if (filtradas.isEmpty()) {
            item {
                Text(
                    "No se encontró ninguna ciudad",
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/** Ciudad más cálida y más fría del momento. */
@Composable
private fun Resumen(datos: List<ClimaCiudad>) {
    val conDatos = datos.filterNot { it.actual.temperatura.isNaN() }
    val calida = conDatos.maxByOrNull { it.actual.temperatura } ?: return
    val fria = conDatos.minByOrNull { it.actual.temperatura } ?: return
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        ResumenItem("🔥 Más cálida", calida, Modifier.weight(1f))
        ResumenItem("🧊 Más fría", fria, Modifier.weight(1f))
    }
}

@Composable
private fun ResumenItem(titulo: String, clima: ClimaCiudad, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(titulo, style = MaterialTheme.typography.labelMedium)
            Text(grados(clima.actual.temperatura), fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text(clima.ciudad.nombre, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun TarjetaCiudad(clima: ClimaCiudad, onClick: () -> Unit) {
    val (texto, emoji) = descripcionClima(clima.actual.codigo, clima.actual.esDeDia)
    val hoy = clima.pronostico.firstOrNull()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(emoji, fontSize = 34.sp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(clima.ciudad.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    "${clima.ciudad.departamento} · $texto",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(grados(clima.actual.temperatura), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                if (hoy != null) {
                    Text(
                        "↑${grados(hoy.maxima)} ↓${grados(hoy.minima)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorMensaje(mensaje: String, onReintentar: () -> Unit, modifier: Modifier) {
    Column(modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("😕", fontSize = 48.sp)
        Spacer(Modifier.height(8.dp))
        Text("No se pudo cargar el clima", style = MaterialTheme.typography.titleMedium)
        Text(mensaje, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onReintentar) { Text("Reintentar") }
    }
}

private fun sinTildes(texto: String): String =
    Normalizer.normalize(texto.trim().lowercase(), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
