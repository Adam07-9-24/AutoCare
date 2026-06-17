// viewmodel/AutoCareViewModelFactory.kt
package com.tuequipo.autocare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tuequipo.autocare.data.repository.AutoCareRepository

class AutoCareViewModelFactory(
    private val repository: AutoCareRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(VehiculoViewModel::class.java) ->
                VehiculoViewModel(repository) as T
            modelClass.isAssignableFrom(MantenimientoViewModel::class.java) ->
                MantenimientoViewModel(repository) as T
            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}
