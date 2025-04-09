package es.masanz.pathfinder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Actividad principal que gestiona la navegación inferior entre los fragmentos
 * de Mis Rutas, Mapa y Explorar Rutas.
 *
 * Esta clase se encarga de cargar el fragmento correspondiente al ítem seleccionado
 * en el BottomNavigationView, y muestra por defecto el fragmento de Mis Rutas al iniciar.
 *
 * @author Izan Ramos
 */
class NavigationActivity : AppCompatActivity() {

    /**
     * Método principal de inicialización de la actividad.
     * Carga el layout y configura el comportamiento del menú de navegación inferior.
     *
     * @param savedInstanceState estado previamente guardado, si existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_mis_rutas -> {
                    loadFragment(MisRutasFragment())
                    true
                }
                R.id.nav_mapa -> {
                    loadFragment(MapaFragment())
                    true
                }
                R.id.nav_explorar -> {
                    loadFragment(ExplorarRutasFragment())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.nav_mapa
            loadFragment(MapaFragment())
        }
    }

    /**
     * Reemplaza el fragmento actual por el proporcionado.
     *
     * @param fragment Fragmento a mostrar en el contenedor.
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

}
