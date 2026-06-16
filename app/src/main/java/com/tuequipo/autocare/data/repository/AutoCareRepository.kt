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
    suspend fun insertarVehiculo(vehiculo: Vehiculo) = vehiculoDao.insert(vehiculo.toEntity())
    fun obtenerVehiculos(): Flow<List<Vehiculo>> = vehiculoDao.getAll().map { list -> list.map { it.toDomain() } }
    suspend fun obtenerVehiculoPorId(id: Int): Vehiculo? = vehiculoDao.getById(id)?.toDomain()
    suspend fun eliminarVehiculo(vehiculo: Vehiculo) = vehiculoDao.delete(vehiculo.toEntity())

    // Mantenimiento CRUD
    suspend fun insertarMantenimiento(m: Mantenimiento) = mantenimientoDao.insert(m.toEntity())
    fun obtenerMantenimientos(): Flow<List<Mantenimiento>> = mantenimientoDao.getAll().map { list -> list.map { it.toDomain() } }
    suspend fun obtenerMantenimientoPorId(id: Int): Mantenimiento? = mantenimientoDao.getById(id)?.toDomain()
    suspend fun actualizarMantenimiento(m: Mantenimiento) = mantenimientoDao.update(m.toEntity())
    suspend fun eliminarMantenimiento(m: Mantenimiento) = mantenimientoDao.delete(m.toEntity())
    
    fun obtenerPorVehiculo(idVehiculo: Int): Flow<List<Mantenimiento>> = 
        mantenimientoDao.getByVehiculo(idVehiculo).map { list -> list.map { it.toDomain() } }
    
    fun obtenerPorEstado(estado: String): Flow<List<Mantenimiento>> = 
        mantenimientoDao.getByEstado(estado).map { list -> list.map { it.toDomain() } }

    // API
    suspend fun obtenerDatosTecnicos(marca: String, modelo: String): Result<List<CarInfoDto>> {
        return try {
            val response = carApiService.getCarInfo(marca, modelo)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
