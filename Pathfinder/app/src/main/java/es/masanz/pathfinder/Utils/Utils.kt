package es.masanz.pathfinder.Utils

import org.osmdroid.util.GeoPoint

/**
 * Utilidades para operaciones geoespaciales.
 *
 * Esta clase incluye una función para decodificar una cadena polilínea codificada
 * usando el algoritmo estándar de Google con precisión 5.
 *
 * @author Izan Ramos
 */
object Utils {
    /**
     * Decodifica una cadena polyline codificada y devuelve una lista de GeoPoint.
     *
     * @param encoded La cadena polyline codificada.
     * @return Lista de GeoPoint resultante.
     */
    fun decodePolyline(encoded: String): List<GeoPoint> {
        val polyline = mutableListOf<GeoPoint>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var shift = 0
            var result = 0
            var b: Int
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if ((result and 1) != 0) -(result shr 1) else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if ((result and 1) != 0) -(result shr 1) else result shr 1
            lng += dlng

            polyline.add(GeoPoint(lat / 1e5, lng / 1e5))
        }
        return polyline
    }
}
