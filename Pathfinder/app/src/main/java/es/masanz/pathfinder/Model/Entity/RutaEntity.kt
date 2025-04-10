package es.masanz.pathfinder.Model.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rutas")
data class RutaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val descripcion: String,
    val dificultad: String,
    val coordenadas: List<Coordenada>
)
