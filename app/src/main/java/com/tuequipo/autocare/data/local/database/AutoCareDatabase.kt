package com.tuequipo.autocare.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tuequipo.autocare.data.local.dao.MantenimientoDao
import com.tuequipo.autocare.data.local.dao.VehiculoDao
import com.tuequipo.autocare.data.local.entity.MantenimientoEntity
import com.tuequipo.autocare.data.local.entity.VehiculoEntity

@Database(entities = [VehiculoEntity::class, MantenimientoEntity::class], version = 1, exportSchema = false)
abstract class AutoCareDatabase : RoomDatabase() {
    abstract fun vehiculoDao(): VehiculoDao
    abstract fun mantenimientoDao(): MantenimientoDao

    companion object {
        @Volatile
        private var INSTANCE: AutoCareDatabase? = null

        fun getDatabase(context: Context): AutoCareDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AutoCareDatabase::class.java,
                    "autocare_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
