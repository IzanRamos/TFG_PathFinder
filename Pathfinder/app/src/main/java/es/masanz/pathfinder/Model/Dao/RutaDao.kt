package es.masanz.pathfinder.Model.Dao

import es.masanz.pathfinder.Model.Entity.RutaEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RutaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRuta(ruta: RutaEntity)

    @Query("SELECT * FROM rutas")
    suspend fun getAllRutas(): List<RutaEntity>

    @Query("SELECT * FROM rutas WHERE id = :id")
    suspend fun getRutaById(id: Int): RutaEntity?
}
