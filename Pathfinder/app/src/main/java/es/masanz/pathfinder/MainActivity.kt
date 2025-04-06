package es.masanz.pathfinder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import org.osmdroid.views.overlay.OverlayItem

class MainActivity : AppCompatActivity() {

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
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
        setContentView(R.layout.activity_main)

        map = findViewById(R.id.map)
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Solicitar permisos de ubicación si no se han concedido aún
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Si ya tenemos permisos, iniciar las actualizaciones de ubicación
            startLocationUpdates()
        } else {
            // Si no se tienen permisos, solicitarlos
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
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
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detener actualizaciones de ubicación al destruir la actividad
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
