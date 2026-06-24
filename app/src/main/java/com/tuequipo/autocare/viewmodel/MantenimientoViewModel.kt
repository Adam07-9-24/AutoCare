package com.tuequipo.autocare.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuequipo.autocare.data.remote.dto.CarInfoDto
import com.tuequipo.autocare.data.repository.AutoCareRepository
import com.tuequipo.autocare.domain.model.Mantenimiento
import com.tuequipo.autocare.domain.model.Vehiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MantenimientoUiState(
    val isLoading: Boolean = false,
    val mantenimientos: List<Mantenimiento> = emptyList(),
    val mantenimientoSeleccionado: Mantenimiento? = null,
    val vehiculoAsociado: Vehiculo? = null,
    val mensajeError: String? = null,
    val consejoApi: String? = null,
    val datosTecnicos: CarInfoDto? = null,
    val errorDatosTecnicos: String? = null,
    val guardadoExitoso: Boolean = false
)

class MantenimientoViewModel(private val repository: AutoCareRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(MantenimientoUiState())
    val uiState: StateFlow<MantenimientoUiState> = _uiState.asStateFlow()

    init {
        cargarMantenimientos()
    }

    fun cargarMantenimientos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.obtenerMantenimientos().collect { lista ->
                _uiState.update { it.copy(isLoading = false, mantenimientos = lista) }
            }
        }
    }

    fun cargarDetalle(idMantenimiento: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mensajeError = null) }
            val mantenimiento = repository.obtenerMantenimientoPorId(idMantenimiento)
            if (mantenimiento != null) {
                _uiState.update { it.copy(isLoading = false, mantenimientoSeleccionado = mantenimiento) }
                val vehiculo = repository.obtenerVehiculoPorId(mantenimiento.idVehiculo)
                if (vehiculo != null) {
                    _uiState.update { it.copy(vehiculoAsociado = vehiculo) }
                    cargarDatosTecnicos(vehiculo.marca, vehiculo.modelo)
                }
            } else {
                _uiState.update { it.copy(isLoading = false, mensajeError = "Mantenimiento no encontrado") }
            }
        }
    }

    fun guardarMantenimiento(m: Mantenimiento) {
        viewModelScope.launch {
            try {
                repository.insertarMantenimiento(m)
                _uiState.update { it.copy(guardadoExitoso = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensajeError = "Error al guardar") }
            }
        }
    }

    fun editarMantenimiento(m: Mantenimiento) {
        viewModelScope.launch {
            try {
                repository.actualizarMantenimiento(m)
                _uiState.update { it.copy(guardadoExitoso = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensajeError = "Error al editar") }
            }
        }
    }

    fun eliminarMantenimiento(id: Int) {
        viewModelScope.launch {
            val m = repository.obtenerMantenimientoPorId(id)
            if (m != null) {
                repository.eliminarMantenimiento(m)
            }
        }
    }

    private suspend fun cargarDatosTecnicos(marca: String, modelo: String) {
        try {
            Log.d("API_DEBUG", "Consultando API → make='$marca' model='$modelo'")
            _uiState.update { it.copy(datosTecnicos = null, errorDatosTecnicos = null) }
            repository.obtenerDatosTecnicos(marca, modelo)
                .onSuccess { autos ->
                    val auto = autos.firstOrNull()
                    Log.d("API_DEBUG", "Datos técnicos recibidos: ${auto != null}")
                    _uiState.update {
                        it.copy(
                            datosTecnicos = auto,
                            errorDatosTecnicos = if (auto == null) "No se encontró información técnica para este vehículo." else null
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(errorDatosTecnicos = "No se pudo cargar información técnica.") }
                }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error al cargar datos técnicos: ${e.message}", e)
            _uiState.update { it.copy(errorDatosTecnicos = "No se pudo cargar información técnica.") }
        }
    }

    fun resetGuardado() {
        _uiState.update { it.copy(guardadoExitoso = false) }
    }
}
