// domain/model/Vehiculo.kt
package com.tuequipo.autocare.domain.model

data class Vehiculo(
    val idVehiculo: Int = 0,
    val marca: String,
    val modelo: String,
    val placa: String,
    val tipoVehiculo: String
)
