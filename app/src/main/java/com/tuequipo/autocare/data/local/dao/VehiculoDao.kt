// data/local/dao/VehiculoDao.kt
package com.tuequipo.autocare.data.local.dao

import androidx.room.*
import com.tuequipo.autocare.data.local.entity.VehiculoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehiculoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehiculo(vehiculo: VehiculoEntity)

    @Query("SELECT * FROM vehiculos")
    fun getAllVehiculos(): Flow<List<VehiculoEntity>>

    @Query("SELECT * FROM vehiculos WHERE idVehiculo = :id")
    suspend fun getVehiculoById(id: Int): VehiculoEntity?

    @Delete
    suspend fun deleteVehiculo(vehiculo: VehiculoEntity)
}
