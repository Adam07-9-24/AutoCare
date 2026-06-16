// viewmodel/MantenimientoViewModel.kt
package com.tuequipo.autocare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuequipo.autocare.data.repository.AutoCareRepository
import com.tuequipo.autocare.domain.model.Mantenimiento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MantenimientoUiState(
    val isLoading: Boolean = false,
    val mantenimientos: List<Mantenimiento> = emptyList(),
    val mantenimientoSeleccionado: Mantenimiento? = null,
    val mensajeError: String? = null,
    val consejoApi: String? = null,
    val guardadoExitoso: Boolean = false
)

class MantenimientoViewModel(
    private val repository: AutoCareRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MantenimientoUiState())
    val uiState: StateFlow<MantenimientoUiState> = _uiState.asStateFlow()

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
            } else {
                _uiState.update { it.copy(isLoading = false, mensajeError = "Mantenimiento no encontrado") }
            }
        }
    }

    fun guardarMantenimiento(mantenimiento: Mantenimiento) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, guardadoExitoso = false, mensajeError = null) }
                repository.insertarMantenimiento(mantenimiento)
                _uiState.update { it.copy(isLoading = false, guardadoExitoso = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, mensajeError = "Error al guardar: ${e.message}") }
            }
        }
    }

    fun editarMantenimiento(mantenimiento: Mantenimiento) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, guardadoExitoso = false, mensajeError = null) }
                repository.actualizarMantenimiento(mantenimiento)
                _uiState.update { it.copy(isLoading = false, guardadoExitoso = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, mensajeError = "Error al editar: ${e.message}") }
            }
        }
    }

    fun eliminarMantenimiento(idMantenimiento: Int) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, mensajeError = null) }
                val mantenimiento = repository.obtenerMantenimientoPorId(idMantenimiento)
                mantenimiento?.let {
                    repository.eliminarMantenimiento(it)
                }
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, mensajeError = "Error al eliminar: ${e.message}") }
            }
        }
    }

    fun cargarDatosTecnicos(marca: String, modelo: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mensajeError = null, consejoApi = null) }
            repository.obtenerDatosTecnicos(marca, modelo)
                .onSuccess { infoList ->
                    if (infoList.isNotEmpty()) {
                        val info = infoList[0]
                        val detalle = "Año: ${info.year}, Cilindros: ${info.cylinders}, Combustible: ${info.fuelType}, MPG Ciudad: ${info.cityMpg}"
                        _uiState.update { it.copy(isLoading = false, consejoApi = detalle) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, mensajeError = "No se pudo cargar información técnica del vehículo.") }
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false, mensajeError = "No se pudo cargar información técnica del vehículo.") }
                }
        }
    }
}
