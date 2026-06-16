// data/local/dao/VehiculoDao.kt
package com.tuequipo.autocare.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tuequipo.autocare.data.local.entity.VehiculoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehiculoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehiculo: VehiculoEntity)

    @Query("SELECT * FROM vehiculos")
    fun getAll(): Flow<List<VehiculoEntity>>

    @Query("SELECT * FROM vehiculos WHERE idVehiculo = :id")
    suspend fun getById(id: Int): VehiculoEntity?

    @Delete
    suspend fun delete(vehiculo: VehiculoEntity)
}
