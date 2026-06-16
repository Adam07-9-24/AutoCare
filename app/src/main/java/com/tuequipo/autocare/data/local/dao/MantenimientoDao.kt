// data/local/dao/MantenimientoDao.kt
package com.tuequipo.autocare.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tuequipo.autocare.data.local.entity.MantenimientoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MantenimientoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mantenimiento: MantenimientoEntity)

    @Query("SELECT * FROM mantenimientos")
    fun getAll(): Flow<List<MantenimientoEntity>>

    @Query("SELECT * FROM mantenimientos WHERE idMantenimiento = :id")
    suspend fun getById(id: Int): MantenimientoEntity?

    @Update
    suspend fun update(mantenimiento: MantenimientoEntity)

    @Delete
    suspend fun delete(mantenimiento: MantenimientoEntity)

    @Query("SELECT * FROM mantenimientos WHERE idVehiculo = :idVehiculo")
    fun getByVehiculo(idVehiculo: Int): Flow<List<MantenimientoEntity>>

    @Query("SELECT * FROM mantenimientos WHERE estado = :estado")
    fun getByEstado(estado: String): Flow<List<MantenimientoEntity>>
}
