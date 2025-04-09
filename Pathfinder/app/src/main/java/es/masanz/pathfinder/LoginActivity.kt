package es.masanz.pathfinder

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * Actividad de inicio de sesión.
 * Permite al usuario autenticarse con su correo y contraseña.
 * Si el checkbox "mantener sesión" está marcado, guarda el UID en SharedPreferences.
 * @author Izan Ramos
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        setup()
    }

    /**
     * Configura los listeners para los botones de login y registro.
     */
    private fun setup() {
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerBtn = findViewById<Button>(R.id.registerButton)
        val emailEt = findViewById<EditText>(R.id.emailEditText)
        val passEt = findViewById<EditText>(R.id.passwordEditText)
        val checkBox = findViewById<CheckBox>(R.id.mantenerSesion)

        loginButton.setOnClickListener {
            val emailText = emailEt.text.toString()
            val passText = passEt.text.toString()

            if (emailText.isNotEmpty() && passText.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailText, passText)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = task.result?.user
                            if (user != null) {
                                if (checkBox.isChecked) {
                                    val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
                                    prefs.edit().putString("uid", user.uid).apply()
                                }
                                showHome()
                            }
                        } else {
                            mostrarAlerta("Error en las credenciales")
                        }
                    }
            } else {
                mostrarAlerta("Por favor ingrese correo y contraseña")
            }
        }

        registerBtn.setOnClickListener {
            showRegister()
        }
    }

    /**
     * Muestra un mensaje toast.
     * @param mensaje El texto del mensaje.
     */
    private fun mostrarAlerta(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    /**
     * Navega a la actividad principal (Home).
     */
    private fun showHome() {
        val homeIntent = Intent(this, NavigationActivity::class.java)
        startActivity(homeIntent)
        finish()
    }

    /**
     * Navega a la actividad de registro.
     */
    private fun showRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
