// data/repository/AutoCareRepository.kt
package com.tuequipo.autocare.data.repository

import com.tuequipo.autocare.data.local.dao.MantenimientoDao
import com.tuequipo.autocare.data.local.dao.VehiculoDao
import com.tuequipo.autocare.data.local.entity.MantenimientoEntity
import com.tuequipo.autocare.data.local.entity.VehiculoEntity
import com.tuequipo.autocare.data.remote.api.CarApiService
import com.tuequipo.autocare.data.remote.dto.CarInfoDto
import com.tuequipo.autocare.domain.model.Mantenimiento
import com.tuequipo.autocare.domain.model.Vehiculo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AutoCareRepository(
    private val vehiculoDao: VehiculoDao,
    private val mantenimientoDao: MantenimientoDao,
    private val carApiService: CarApiService
) {
    // Mappers
    private fun VehiculoEntity.toDomain() = Vehiculo(idVehiculo, marca, modelo, placa, tipoVehiculo)
    private fun Vehiculo.toEntity() = VehiculoEntity(idVehiculo, marca, modelo, placa, tipoVehiculo)

    private fun MantenimientoEntity.toDomain() = Mantenimiento(
        idMantenimiento, idVehiculo, titulo, descripcion, tipoMantenimiento, fechaProgramada, estado, recordatorioActivo
    )
    private fun Mantenimiento.toEntity() = MantenimientoEntity(
        idMantenimiento, idVehiculo, titulo, descripcion, tipoMantenimiento, fechaProgramada, estado, recordatorioActivo
    )

    // Vehiculo CRUD
    suspend fun insertVehiculo(vehiculo: Vehiculo) = vehiculoDao.insertVehiculo(vehiculo.toEntity())
    fun getAllVehiculos(): Flow<List<Vehiculo>> = vehiculoDao.getAllVehiculos().map { list -> list.map { it.toDomain() } }
    suspend fun getVehiculoById(id: Int): Vehiculo? = vehiculoDao.getVehiculoById(id)?.toDomain()
    suspend fun deleteVehiculo(vehiculo: Vehiculo) = vehiculoDao.deleteVehiculo(vehiculo.toEntity())

    // Mantenimiento CRUD
    suspend fun insertMantenimiento(mantenimiento: Mantenimiento) = mantenimientoDao.insertMantenimiento(mantenimiento.toEntity())
    fun getAllMantenimientos(): Flow<List<Mantenimiento>> = mantenimientoDao.getAllMantenimientos().map { list -> list.map { it.toDomain() } }
    suspend fun getMantenimientoById(id: Int): Mantenimiento? = mantenimientoDao.getMantenimientoById(id)?.toDomain()
    suspend fun updateMantenimiento(mantenimiento: Mantenimiento) = mantenimientoDao.updateMantenimiento(mantenimiento.toEntity())
    suspend fun deleteMantenimiento(mantenimiento: Mantenimiento) = mantenimientoDao.deleteMantenimiento(mantenimiento.toEntity())
    fun getMantenimientosByVehiculo(vehiculoId: Int): Flow<List<Mantenimiento>> = 
        mantenimientoDao.getMantenimientosByVehiculo(vehiculoId).map { list -> list.map { it.toDomain() } }
    
    // Remote API
    suspend fun getCarTechnicalInfo(marca: String, modelo: String): List<CarInfoDto> {
        return carApiService.getCarInfo(marca, modelo)
    }
}
