package es.masanz.pathfinder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

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
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapaFragment : Fragment() {

    private lateinit var map: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    // Marcador de ubicación
    private var userMarker: Marker? = null

    // Permiso de ubicación solicitado
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permiso concedido, iniciamos la actualización de la ubicación
                startLocationUpdates()
            } else {
                // Permiso denegado, muestra un mensaje al usuario
                Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inicializa la configuración de OSMDroid
        Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("osmdroid", 0))

        // Infla el layout y configura el mapa
        val view = inflater.inflate(R.layout.fragment_mapa, container, false)
        map = view.findViewById(R.id.map)

        // Configuración del mapa
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        // Ubicación inicial (Ejemplo: Madrid)
        val startPoint = GeoPoint(40.4168, -3.7038)
        val mapController = map.controller
        mapController.setZoom(15.0)
        mapController.setCenter(startPoint)

        // Evitar superposición de tiles
        map.overlayManager.tilesOverlay.setLoadingBackgroundColor(android.graphics.Color.TRANSPARENT)

        // Configuración de FusedLocationProvider
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Solicitar permisos de ubicación si no se han concedido aún
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Si ya tenemos permisos, iniciar las actualizaciones de ubicación
            startLocationUpdates()
        } else {
            // Si no se tienen permisos, solicitarlos
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        return view
    }

    private fun startLocationUpdates() {
        // Configuración de la solicitud de ubicación
        locationRequest = LocationRequest.create().apply {
            interval = 10000L // Intervalo de actualización (10 segundos)
            fastestInterval = 5000L // Intervalo más rápido (5 segundos)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    // Cuando recibimos la ubicación, actualizamos el mapa
                    val latitude = location.latitude
                    val longitude = location.longitude
                    updateMapLocation(latitude, longitude)
                }
            }
        }

        // Iniciar las actualizaciones de ubicación
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no tenemos permisos de ubicación, no continuamos
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun updateMapLocation(latitude: Double, longitude: Double) {
        val userLocation = GeoPoint(latitude, longitude)
        val mapController = map.controller
        mapController.setCenter(userLocation)
        mapController.setZoom(15.0)

        // Si el marcador ya existe, solo actualizamos su posición
        if (userMarker != null) {
            userMarker!!.position = userLocation
        } else {
            // Si no existe el marcador, creamos uno nuevo
            userMarker = Marker(map)
            userMarker!!.position = userLocation
            userMarker!!.title = "Tu Ubicación"

            // Cambiar el icono del marcador a un puntito azul
            userMarker!!.setIcon(resources.getDrawable(R.drawable.ubicacion, null))

            // Agregar el marcador al mapa
            map.overlays.add(userMarker)
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()  // Asegura que el mapa se reanude
    }

    override fun onPause() {
        super.onPause()
        map.onPause()  // Pausa el mapa cuando la actividad está en pausa
    }

  override fun onDestroyView() {
        super.onDestroyView()
        // Detener actualizaciones de ubicación cuando el fragmento se destruye
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
