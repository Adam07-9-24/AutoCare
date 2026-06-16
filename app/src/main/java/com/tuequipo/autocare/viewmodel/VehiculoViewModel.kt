// viewmodel/VehiculoViewModel.kt
package com.tuequipo.autocare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuequipo.autocare.data.repository.AutoCareRepository
import com.tuequipo.autocare.domain.model.Vehiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VehiculoUiState(
    val isLoading: Boolean = false,
    val vehiculos: List<Vehiculo> = emptyList(),
    val vehiculoSeleccionado: Vehiculo? = null,
    val mensajeError: String? = null,
    val consejoApi: String? = null
)

class VehiculoViewModel(
    private val repository: AutoCareRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VehiculoUiState())
    val uiState: StateFlow<VehiculoUiState> = _uiState.asStateFlow()

    fun cargarVehiculos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getAllVehiculos().collect { lista ->
                _uiState.update { it.copy(isLoading = false, vehiculos = lista) }
            }
        }
    }

    fun agregarVehiculo(vehiculo: Vehiculo) {
        viewModelScope.launch {
            try {
                repository.insertVehiculo(vehiculo)
            } catch (e: Exception) {
                _uiState.update { it.copy(mensajeError = "Error al agregar vehículo") }
            }
        }
    }

    fun eliminarVehiculo(vehiculo: Vehiculo) {
        viewModelScope.launch {
            try {
                repository.deleteVehiculo(vehiculo)
            } catch (e: Exception) {
                _uiState.update { it.copy(mensajeError = "Error al eliminar vehículo") }
            }
        }
    }

    fun obtenerDetallesTecnicos(vehiculo: Vehiculo) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mensajeError = null, consejoApi = null) }
            try {
                val info = repository.getCarTechnicalInfo(vehiculo.marca, vehiculo.modelo)
                if (info.isNotEmpty()) {
                    val car = info[0]
                    val techInfo = "Año: ${car.year}, Cilindros: ${car.cylinders}, Combustible: ${car.fuelType}, MPG Ciudad: ${car.cityMpg}, MPG Autopista: ${car.highwayMpg}"
                    _uiState.update { it.copy(isLoading = false, consejoApi = techInfo) }
                } else {
                    _uiState.update { it.copy(isLoading = false, mensajeError = "No se pudo cargar información técnica del vehículo.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, mensajeError = "No se pudo cargar información técnica del vehículo.") }
            }
        }
    }
}
