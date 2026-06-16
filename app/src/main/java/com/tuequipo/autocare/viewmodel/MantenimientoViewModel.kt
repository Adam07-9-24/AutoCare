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
            repository.getAllMantenimientos().collect { lista ->
                _uiState.update { it.copy(isLoading = false, mantenimientos = lista) }
            }
        }
    }

    fun cargarMantenimientoPorId(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val mantenimiento = repository.getMantenimientoById(id)
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
                _uiState.update { it.copy(isLoading = true, guardadoExitoso = false) }
                repository.insertMantenimiento(mantenimiento)
                _uiState.update { it.copy(isLoading = false, guardadoExitoso = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, mensajeError = "Error al guardar: ${e.message}") }
            }
        }
    }

    fun editarMantenimiento(mantenimiento: Mantenimiento) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                repository.updateMantenimiento(mantenimiento)
                _uiState.update { it.copy(isLoading = false, guardadoExitoso = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, mensajeError = "Error al actualizar: ${e.message}") }
            }
        }
    }

    fun eliminarMantenimiento(mantenimiento: Mantenimiento) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                repository.deleteMantenimiento(mantenimiento)
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, mensajeError = "Error al eliminar: ${e.message}") }
            }
        }
    }
}
