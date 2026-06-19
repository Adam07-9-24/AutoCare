// viewmodel/MantenimientoViewModel.kt
package com.tuequipo.autocare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuequipo.autocare.data.remote.dto.CarInfoDto
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
    val isLoadingDatosTecnicos: Boolean = false,
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
            _uiState.update { it.copy(isLoadingDatosTecnicos = true, consejoApi = null) }
            repository.obtenerDatosTecnicos(marca, modelo)
                .onSuccess { infoList ->
                    val detalle = infoList.firstOrNull()?.let { formatearDatosTecnicos(it) }
                        ?: "No se encontr\u00f3 informaci\u00f3n t\u00e9cnica para este modelo."

                    _uiState.update { it.copy(isLoadingDatosTecnicos = false, consejoApi = detalle) }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoadingDatosTecnicos = false,
                            consejoApi = "No se pudo cargar informaci\u00f3n t\u00e9cnica del veh\u00edculo."
                        )
                    }
                }
        }
    }

    private fun formatearDatosTecnicos(info: CarInfoDto): String {
        val campos = buildList {
            agregarCampo("A\u00f1o", info.year)
            agregarCampo("Clase", info.vehicleClass.formatearCapitalizado())
            agregarCampo("Cilindros", info.cylinders)
            agregarCampo("Desplazamiento", info.displacement?.let { "${it}L" })
            agregarCampo("Combustible", info.fuelType.formatearCapitalizado())
            agregarCampo("Transmisi\u00f3n", info.transmission.formatearTransmision())
            agregarCampo("Tracci\u00f3n", info.drive?.uppercase())
            agregarCampo("MPG ciudad", info.cityMpg)
            agregarCampo("MPG carretera", info.highwayMpg)
            agregarCampo("MPG combinado", info.combinationMpg)
        }

        return campos.ifEmpty {
            listOf("No se encontr\u00f3 informaci\u00f3n t\u00e9cnica para este modelo.")
        }.joinToString(separator = "\n")
    }

    private fun MutableList<String>.agregarCampo(etiqueta: String, valor: Any?) {
        val texto = valor?.toString()?.takeIf { it.isNotBlank() } ?: return
        add("$etiqueta: $texto")
    }

    private fun String?.formatearCapitalizado(): String? {
        val texto = this?.trim()?.takeIf { it.isNotBlank() } ?: return null
        return texto.lowercase().replaceFirstChar { it.uppercase() }
    }

    private fun String?.formatearTransmision(): String? {
        val texto = this?.trim()?.takeIf { it.isNotBlank() } ?: return null
        return when (texto.lowercase()) {
            "a" -> "Autom\u00e1tica"
            "m" -> "Manual"
            else -> texto.formatearCapitalizado()
        }
    }
}
