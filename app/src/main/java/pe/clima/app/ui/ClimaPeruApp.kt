package pe.clima.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.clima.app.ClimaViewModel
import pe.clima.app.EstadoClima

private val ColoresClaros = lightColorScheme(
    primary = Color(0xFF1E6FD9),
    secondary = Color(0xFFD62828),
    primaryContainer = Color(0xFFD8E6FF),
)
private val ColoresOscuros = darkColorScheme(
    primary = Color(0xFF9CC2FF),
    secondary = Color(0xFFFF8A80),
    primaryContainer = Color(0xFF1B3A66),
)

@Composable
fun ClimaPeruApp(vm: ClimaViewModel = viewModel()) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) ColoresOscuros else ColoresClaros) {
        val estado by vm.estado.collectAsStateWithLifecycle()
        var seleccionada by rememberSaveable { mutableStateOf<String?>(null) }

        val datos = (estado as? EstadoClima.Listo)?.datos
        val climaSeleccionado = datos?.firstOrNull { it.ciudad.nombre == seleccionada }

        if (climaSeleccionado != null) {
            BackHandler { seleccionada = null }
            DetalleScreen(clima = climaSeleccionado, onVolver = { seleccionada = null })
        } else {
            ListaScreen(
                estado = estado,
                onRecargar = vm::cargar,
                onSeleccionar = { seleccionada = it.ciudad.nombre },
            )
        }
    }
}
