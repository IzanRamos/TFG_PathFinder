package es.masanz.pathfinder

import es.masanz.pathfinder.Model.Entity.OsrmResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OsrmService {

    /**
     * Solicita a OSRM una ruta para el perfil 'driving' con las coordenadas dadas.
     *
     * @param coordinates Las coordenadas en formato "long1,lat1;long2,lat2;...".
     * @param geometries  (Opcional) Tipo de geometría a devolver (default: "polyline").
     * @param overview    (Opcional) Tipo de resumen de la ruta (default: "full").
     * @return Un Call que al ejecutarse devuelve un OsrmResponse.
     */
    @GET("route/v1/driving/{coordinates}")
    fun getRoute(
        @Path("coordinates") coordinates: String,
        @Query("geometries") geometries: String = "polyline",
        @Query("overview") overview: String = "full"
    ): Call<OsrmResponse>
}
