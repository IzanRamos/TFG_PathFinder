package es.masanz.pathfinder

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity que maneja la pantalla de splash, mostrando el logo de la aplicación.
 *
 * Esta actividad se encarga de mostrar el logo con una animación de aparición (fade-in)
 * y después de un breve retraso, redirige a la actividad principal de la aplicación.
 *
 * @author Izan Ramos
 */
class SplashActivity : AppCompatActivity() {

    /**
     * Método que se ejecuta al crear la actividad.
     * Inicializa el layout y aplica la animación al logo.
     * También maneja el retraso para redirigir a la siguiente actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashLogo = findViewById<ImageView>(R.id.splash_logo)

        // Animación de aparición del logo
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 1500
        fadeIn.startOffset = 500
        splashLogo.startAnimation(fadeIn)

        // Retraso para redirigir
        Handler().postDelayed({
            checkSession()
        }, 3000)
    }

    /**
     * Verifica si existe un `uid` guardado en las preferencias compartidas
     * y redirige al usuario a la actividad correspondiente.
     */
    private fun checkSession() {
        val sharedPrefs: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)

        // Comprobamos si el `uid` está guardado
        val userUid = sharedPrefs.getString("uid", null)

        if (userUid != null) {
            // Si hay un uid guardado, redirigir a HomeActivity
            goToHome()
        } else {
            // Si no hay uid guardado, redirigir a LoginActivity
            goToLogin()
        }
    }

    /**
     * Redirige al usuario a la actividad principal de la aplicación.
     *
     * @see HomeActivity
     */
    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Redirige al usuario a la actividad de login.
     *
     * @see LoginActivity
     */
    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
