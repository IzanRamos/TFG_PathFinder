package es.masanz.pathfinder

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * Actividad principal de la aplicación, donde el usuario es redirigido después de iniciar sesión.
 * En esta pantalla se muestran botones de prueba y un botón para cerrar sesión.
 *
 * @author Izan Ramos
 */
class HomeActivity : AppCompatActivity() {

    /**
     * Método que se ejecuta cuando la actividad es creada.
     * Configura los botones y sus funcionalidades.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setup()
    }

    /**
     * Configura la funcionalidad de los botones en la pantalla de inicio.
     */
    private fun setup() {
        title = "Inicio"

        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val testButton = findViewById<Button>(R.id.testButton)

        // Configura la funcionalidad para el botón de "Cerrar Sesión"
        logoutButton.setOnClickListener {
            logout()
        }

        // Configura el botón de prueba (aquí puedes agregar la funcionalidad que desees)
        testButton.setOnClickListener {
            // Realizar alguna acción de prueba
            showToast("Botón de prueba presionado")
        }
    }

    /**
     * Método para cerrar sesión de Firebase, eliminar el uid de las SharedPreferences
     * y redirigir al LoginActivity.
     */
    private fun logout() {
        // Borrar el UID de SharedPreferences

        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        prefs.edit().remove("uid").apply()

        // Cerrar sesión en Firebase
        FirebaseAuth.getInstance().signOut()

        // Mostrar mensaje de cierre de sesión
        showToast("Sesión cerrada exitosamente")

        // Redirigir al login
        redirectToLogin()
    }

    /**
     * Muestra un Toast con el mensaje proporcionado.
     *
     * @param message Mensaje que se mostrará en el Toast.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Redirige al usuario a la pantalla de login.
     */
    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
