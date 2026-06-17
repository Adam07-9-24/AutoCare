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

class VehiculoViewModel(
    private val repository: AutoCareRepository
) : ViewModel() {

    private val _vehiculos = MutableStateFlow<List<Vehiculo>>(emptyList())
    val vehiculos: StateFlow<List<Vehiculo>> = _vehiculos.asStateFlow()

    private val _vehiculoSeleccionado = MutableStateFlow<Vehiculo?>(null)
    val vehiculoSeleccionado: StateFlow<Vehiculo?> = _vehiculoSeleccionado.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _consejoApi = MutableStateFlow<String?>(null)
    val consejoApi: StateFlow<String?> = _consejoApi.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    fun cargarVehiculos() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.obtenerVehiculos().collect { lista ->
                _vehiculos.value = lista
                _isLoading.value = false
            }
        }
    }

    fun cargarDetalle(idVehiculo: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _mensajeError.value = null
                _vehiculoSeleccionado.value = repository.obtenerVehiculoPorId(idVehiculo)
                if (_vehiculoSeleccionado.value == null) {
                    _mensajeError.value = "Vehiculo no encontrado"
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _mensajeError.value = "Error al cargar vehiculo"
            }
        }
    }

    fun agregarVehiculo(vehiculo: Vehiculo) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.insertarVehiculo(vehiculo)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _mensajeError.value = "Error al agregar vehículo"
            }
        }
    }

    fun editarVehiculo(vehiculo: Vehiculo) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.actualizarVehiculo(vehiculo)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _mensajeError.value = "Error al editar vehiculo"
            }
        }
    }

    fun eliminarVehiculo(idVehiculo: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val vehiculo = repository.obtenerVehiculoPorId(idVehiculo)
                vehiculo?.let {
                    repository.eliminarVehiculo(it)
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _mensajeError.value = "Error al eliminar vehículo"
            }
        }
    }

    fun obtenerDatosTecnicos(marca: String, modelo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _mensajeError.value = null
            _consejoApi.value = null
            
            repository.obtenerDatosTecnicos(marca, modelo)
                .onSuccess { infoList ->
                    if (infoList.isNotEmpty()) {
                        val info = infoList[0]
                        val techInfo = "Marca: ${info.make}, Modelo: ${info.model}, Año: ${info.year}, Cilindros: ${info.cylinders}, Combustible: ${info.fuelType}"
                        _consejoApi.value = techInfo
                    } else {
                        _mensajeError.value = "No se pudo cargar información técnica del vehículo."
                    }
                    _isLoading.value = false
                }
                .onFailure {
                    _mensajeError.value = "No se pudo cargar información técnica del vehículo."
                    _isLoading.value = false
                }
        }
    }
}
