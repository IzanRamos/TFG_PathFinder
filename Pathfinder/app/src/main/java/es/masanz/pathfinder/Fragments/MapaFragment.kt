package es.masanz.pathfinder

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import es.masanz.pathfinder.Model.Entity.OsrmResponse
import es.masanz.pathfinder.Utils.Utils
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Fragment que permite crear rutas marcando puntos y muestra la ruta real calculada mediante OSRM.
 * La ruta se pinta como una línea azul que sigue el camino real (no simplemente conectando los puntos).
 *
 * @author Izan Ramos
 */
class MapaFragment : Fragment() {

    // Configuración Retrofit para OSRM
    val retrofit = Retrofit.Builder()
        .baseUrl("https://router.project-osrm.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val osrmService = retrofit.create(OsrmService::class.java)

    private val marcadores = mutableListOf<Marker>()

    private lateinit var map: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    // Lista de puntos seleccionados para la ruta
    private val rutaPuntos = mutableListOf<GeoPoint>()
    private lateinit var polyline: Polyline

    private var enModoRuta = false

    private var userMarker: Marker? = null

    // Permiso de ubicación
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startLocationUpdates()
            } else {
                Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("osmdroid", 0))
        val view = inflater.inflate(R.layout.fragment_mapa, container, false)
        map = view.findViewById(R.id.map)

        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val startPoint = GeoPoint(40.4168, -3.7038)
        val mapController = map.controller
        mapController.setZoom(15.0)
        mapController.setCenter(startPoint)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        val btnCrearRuta = view.findViewById<Button>(R.id.btnCrearRuta)
        val btnFinalizar = view.findViewById<Button>(R.id.btnFinalizarRuta)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelarRuta)
        val btnLimpiar = view.findViewById<Button>(R.id.btnLimpiarPuntos)
        val layoutAcciones = view.findViewById<LinearLayout>(R.id.rutaActionsLayout)

        btnCrearRuta.setOnClickListener {
            enModoRuta = true
            inicializarCreacionRuta()
            layoutAcciones.visibility = View.VISIBLE
            btnCrearRuta.visibility = View.GONE
            mostrarInstrucciones()
        }

        btnCancelar.setOnClickListener {
            enModoRuta = false
            resetearRuta()
            layoutAcciones.visibility = View.GONE
            btnCrearRuta.visibility = View.VISIBLE
        }

        btnLimpiar.setOnClickListener {
            resetearRuta()
        }

        btnFinalizar.setOnClickListener {
            if (rutaPuntos.size >= 2) {
                obtenerRuta()
            } else {
                Toast.makeText(requireContext(), "Añade al menos dos puntos", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    /**
     * Configura el modo de creación de ruta para que al hacer tap en el mapa se agreguen puntos y se actualice la ruta.
     */
    private fun inicializarCreacionRuta() {
        val receiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let {
                    rutaPuntos.add(it)
                    agregarMarcador(it)
                    // Aquí llamamos a obtenerRuta para actualizar la ruta en tiempo real
                    obtenerRuta()
                }
                return true
            }
            override fun longPressHelper(p: GeoPoint?): Boolean = false
        }
        val overlayEventos = MapEventsOverlay(receiver)
        map.overlays.add(overlayEventos)
    }

    /**
     * Muestra un diálogo de instrucciones para la creación de la ruta.
     */
    private fun mostrarInstrucciones() {
        AlertDialog.Builder(requireContext())
            .setTitle("Modo Crear Ruta")
            .setMessage(
                "• Toca el mapa para añadir puntos.\n" +
                        "• La ruta real se actualizará con cada nuevo punto.\n" +
                        "• Usa 'Limpiar' para borrar la ruta y empezar de nuevo.\n" +
                        "• Pulsa 'Finalizar' cuando termines."
            )
            .setPositiveButton("Entendido", null)
            .show()
    }

    /**
     * Agrega un marcador visual en el punto indicado.
     *
     * @param punto La coordenada donde se coloca el marcador.
     */
    private fun agregarMarcador(punto: GeoPoint) {
        val marcador = Marker(map).apply {
            position = punto
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Punto ${rutaPuntos.size}"
        }
        map.overlays.add(marcador)
        marcadores.add(marcador)
    }

    /**
     * Dibuja la ruta (polilínea) entre los puntos seleccionados.
     */
    private fun dibujarRuta() {
        if (!::polyline.isInitialized) {
            polyline = Polyline().apply {
                color = Color.BLUE
                width = 10f
            }
            map.overlays.add(polyline)
        }
        polyline.setPoints(rutaPuntos)
        map.invalidate()
    }

    /**
     * Limpia la ruta actual: elimina los puntos, marcadores y la polilínea.
     */
    private fun resetearRuta() {
        rutaPuntos.clear()
        marcadores.forEach { map.overlays.remove(it) }
        marcadores.clear()
        if (::polyline.isInitialized) {
            polyline.setPoints(emptyList())
        }
        map.invalidate()
    }

    /**
     * Solicita a OSRM la ruta real entre los puntos marcados y dibuja la polilínea resultante.
     */
    fun obtenerRuta() {
        if (rutaPuntos.size < 2) return

        // Construir las coordenadas en el formato requerido: "long,lat;long,lat"
        val coordinates = rutaPuntos.joinToString(";", transform = { "${it.longitude},${it.latitude}" })
        Log.d("Ruta", "Coordenadas: $coordinates")
        val url = "https://router.project-osrm.org/route/v1/driving/$coordinates?geometries=polyline&overview=full"
        Log.d("Ruta", "URL completa de la solicitud: $url")

        osrmService.getRoute(coordinates).enqueue(object : Callback<OsrmResponse> {
            override fun onResponse(call: Call<OsrmResponse>, response: Response<OsrmResponse>) {
                Log.d("Ruta", "Código de respuesta: ${response.code()}")
                if (response.isSuccessful) {
                    val route = response.body()?.routes?.get(0)
                    val encodedPath = route?.geometry
                    Log.d("Ruta", "Geometría codificada: $encodedPath")
                    if (encodedPath != null) {
                        // Decodificar la polilínea usando la función Utils.decodePolyline
                        val decodedPath = Utils.decodePolyline(encodedPath)
                        Log.d("Ruta", "Puntos decodificados: $decodedPath")
                        // Usamos los puntos decodificados para dibujar la ruta
                        if (!::polyline.isInitialized) {
                            polyline = Polyline().apply {
                                color = Color.BLUE
                                width = 10f
                            }
                            map.overlays.add(polyline)
                        }
                        polyline.setPoints(decodedPath)
                        map.invalidate()
                        Toast.makeText(requireContext(), "Ruta generada con éxito", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error: No se recibió geometría", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: Código ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<OsrmResponse>, t: Throwable) {
                Log.e("Ruta", "Error de conexión: ${t.message}")
                Toast.makeText(requireContext(), "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startLocationUpdates() {
        locationRequest = LocationRequest.create().apply {
            interval = 60000L
            fastestInterval = 5000L
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    updateMapLocation(location.latitude, location.longitude)
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun updateMapLocation(latitude: Double, longitude: Double) {
        val userLocation = GeoPoint(latitude, longitude)
        val mapController = map.controller
        mapController.setCenter(userLocation)
        mapController.setZoom(15.0)
        if (userMarker != null) {
            userMarker!!.position = userLocation
        } else {
            userMarker = Marker(map).apply {
                position = userLocation
                title = "Tu Ubicación"
                setIcon(resources.getDrawable(R.drawable.ubicacion, null))
            }
            map.overlays.add(userMarker)
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
