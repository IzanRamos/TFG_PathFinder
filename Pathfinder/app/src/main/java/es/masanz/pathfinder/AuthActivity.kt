package es.masanz.pathfinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Setup
        setup()
    }

    private fun setup() {
        val loginButton = findViewById<Button>(R.id.loginButton)
        Log.d("AuthActivity", "Login Button: $loginButton")

        val registerBtn = findViewById<Button>(R.id.registerButton)
        Log.d("AuthActivity", "Register Button: $registerBtn")

        val emailet = findViewById<EditText>(R.id.emailEditText)
        Log.d("AuthActivity", "Email EditText: $emailet")

        val passet = findViewById<EditText>(R.id.passwordEditText)
        Log.d("AuthActivity", "Password EditText: $passet")


        title = "Autenticación"

        loginButton.setOnClickListener {
            val emailtext = emailet.text.toString()
            val passtext = passet.text.toString()

            if (emailtext.isNotEmpty() && passtext.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailtext, passtext)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        } else {
                            mostrarAlerta("Error en las credenciales")
                        }
                    }
            } else {
                mostrarAlerta("Por favor ingrese correo y contraseña")
            }
        }

        registerBtn.setOnClickListener {
            val emailtext = emailet.text.toString()
            val passtext = passet.text.toString()

            if (emailtext.isNotEmpty() && passtext.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailtext, passtext)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        } else {
                            mostrarAlerta("Error en las credenciales")
                        }
                    }
            } else {
                mostrarAlerta("Por favor ingrese correo y contraseña")
            }
        }
    }

    private fun mostrarAlerta(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
}