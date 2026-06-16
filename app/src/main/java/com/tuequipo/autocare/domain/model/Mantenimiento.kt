// domain/model/Mantenimiento.kt
package com.tuequipo.autocare.domain.model

data class Mantenimiento(
    val idMantenimiento: Int = 0,
    val idVehiculo: Int,
    val titulo: String,
    val descripcion: String,
    val tipoMantenimiento: String,
    val fechaProgramada: String,
    val estado: String,
    val recordatorioActivo: Boolean
)
