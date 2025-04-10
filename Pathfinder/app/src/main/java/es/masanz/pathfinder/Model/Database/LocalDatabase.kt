package es.masanz.pathfinder.Model.Database

import es.masanz.pathfinder.Model.Dao.RutaDao
import es.masanz.pathfinder.Model.Entity.CoordenadaConverter
import es.masanz.pathfinder.Model.Entity.RutaEntity
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

class LocalDatabase {
    @Database(entities = [RutaEntity::class], version = 1)
    @TypeConverters(CoordenadaConverter::class)
    abstract class LocalDatabase : RoomDatabase() {
        abstract fun rutaDao(): RutaDao

        companion object {
            @Volatile private var INSTANCE: LocalDatabase? = null

            fun getDatabase(context: Context): LocalDatabase {
                return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        LocalDatabase::class.java,
                        "pathfinder.db"
                    ).build()
                    INSTANCE = instance
                    instance
                }
            }
        }
    }
}