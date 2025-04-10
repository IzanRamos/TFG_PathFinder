package es.masanz.pathfinder.Model.Entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Conversor de listas de coordenadas para Room.
 *
 * Convierte una lista de [Coordenada] a String y viceversa usando Gson.
 * Esto es necesario para que Room pueda almacenar listas complejas como strings en la base de datos.
 */
class CoordenadaConverter {

    private val gson = Gson()

    /**
     * Convierte una lista de coordenadas a un JSON string.
     *
     * @param value Lista de objetos [Coordenada].
     * @return String en formato JSON.
     */
    @TypeConverter
    fun fromCoordenadasList(value: List<Coordenada>): String {
        return gson.toJson(value)
    }

    /**
     * Convierte un string JSON a una lista de coordenadas.
     *
     * @param value String en formato JSON.
     * @return Lista de objetos [Coordenada].
     */
    @TypeConverter
    fun toCoordenadasList(value: String): List<Coordenada> {
        val listType = object : TypeToken<List<Coordenada>>() {}.type
        return gson.fromJson(value, listType)
    }
}
