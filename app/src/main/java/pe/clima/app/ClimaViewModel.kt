package pe.clima.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface EstadoClima {
    data object Cargando : EstadoClima
    data class Listo(val datos: List<ClimaCiudad>) : EstadoClima
    data class Error(val mensaje: String) : EstadoClima
}

class ClimaViewModel : ViewModel() {

    private val _estado = MutableStateFlow<EstadoClima>(EstadoClima.Cargando)
    val estado: StateFlow<EstadoClima> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        _estado.value = EstadoClima.Cargando
        viewModelScope.launch {
            _estado.value = try {
                EstadoClima.Listo(ClimaApi.obtenerClima(CIUDADES_PERU))
            } catch (e: Exception) {
                EstadoClima.Error(e.message ?: "No se pudo obtener el clima")
            }
        }
    }
}
