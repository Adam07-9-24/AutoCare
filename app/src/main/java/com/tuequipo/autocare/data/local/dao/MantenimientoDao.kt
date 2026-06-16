// data/local/dao/MantenimientoDao.kt
package com.tuequipo.autocare.data.local.dao

import androidx.room.*
import com.tuequipo.autocare.data.local.entity.MantenimientoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MantenimientoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMantenimiento(mantenimiento: MantenimientoEntity)

    @Query("SELECT * FROM mantenimientos")
    fun getAllMantenimientos(): Flow<List<MantenimientoEntity>>

    @Query("SELECT * FROM mantenimientos WHERE idMantenimiento = :id")
    suspend fun getMantenimientoById(id: Int): MantenimientoEntity?

    @Update
    suspend fun updateMantenimiento(mantenimiento: MantenimientoEntity)

    @Delete
    suspend fun deleteMantenimiento(mantenimiento: MantenimientoEntity)

    @Query("SELECT * FROM mantenimientos WHERE idVehiculo = :vehiculoId")
    fun getMantenimientosByVehiculo(vehiculoId: Int): Flow<List<MantenimientoEntity>>

    @Query("SELECT * FROM mantenimientos WHERE estado = :estado")
    fun getMantenimientosByEstado(estado: String): Flow<List<MantenimientoEntity>>
}
