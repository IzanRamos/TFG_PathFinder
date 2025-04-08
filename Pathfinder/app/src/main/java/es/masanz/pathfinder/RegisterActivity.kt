package es.masanz.pathfinder

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Registro de usuario en la aplicación.
 *
 * Esta actividad permite al usuario crear una cuenta nueva en la aplicación.
 * Se valida la información proporcionada en los campos de nombre, correo, contraseña,
 * confirmación de contraseña y género antes de proceder con el registro en Firebase.
 *
 * @author Izan Ramos
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var nameError: TextView
    private lateinit var emailError: TextView
    private lateinit var passError: TextView
    private lateinit var confirmPassError: TextView

    /**
     * Método llamado al crear la actividad.
     *
     * Inicializa la actividad, configura las vistas y sus interacciones.
     *
     * @param savedInstanceState El estado guardado de la actividad, si existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val nameEt = findViewById<EditText>(R.id.nameEditText)
        val emailEt = findViewById<EditText>(R.id.emailEditText)
        val genderSp = findViewById<Spinner>(R.id.genderSpinner)
        val passEt = findViewById<EditText>(R.id.passwordEditText)
        val confirmPassEt = findViewById<EditText>(R.id.confirmPasswordEditText)
        val registerBtn = findViewById<Button>(R.id.registerButton)
        val cancelBtn = findViewById<Button>(R.id.cancelButton)

        // Mensajes de error debajo de cada campo
        nameError = findViewById(R.id.nameError)
        emailError = findViewById(R.id.emailError)
        passError = findViewById(R.id.passError)
        confirmPassError = findViewById(R.id.confirmPassError)

        // Spinner con opciones de género
        val genderOptions = listOf("Selecciona tu sexo", "Masculino", "Femenino", "Otro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genderOptions)
        genderSp.adapter = adapter

        // Botón de registro
        registerBtn.setOnClickListener {
            val name = nameEt.text.toString().trim()
            val email = emailEt.text.toString().trim()
            val gender = genderSp.selectedItem.toString()
            val password = passEt.text.toString()
            val confirmPassword = confirmPassEt.text.toString()

            if (validateInputs(name, email, gender, password, confirmPassword)) {
                registerUser(name, email, gender, password)
            }
        }

        // Botón de cancelación
        cancelBtn.setOnClickListener {
            finish()
        }

        // Text Watchers para borrar los errores en los campos cuando el usuario comienza a escribir
        nameEt.addTextChangedListener {
            nameError.text = ""
            nameEt.background = ContextCompat.getDrawable(this, R.drawable.edittext_bg)
        }

        emailEt.addTextChangedListener {
            emailError.text = ""
            emailEt.background = ContextCompat.getDrawable(this, R.drawable.edittext_bg)
        }

        passEt.addTextChangedListener {
            passError.text = ""
            passEt.background = ContextCompat.getDrawable(this, R.drawable.edittext_bg)
        }

        confirmPassEt.addTextChangedListener {
            confirmPassError.text = ""
            confirmPassEt.background = ContextCompat.getDrawable(this, R.drawable.edittext_bg)
        }
    }

    /**
     * Método de validación de los campos del formulario de registro.
     *
     * Valida que el nombre, correo, contraseña y confirmación de contraseña sean correctos.
     * También valida que se haya seleccionado un género.
     *
     * @param name El nombre completo del usuario.
     * @param email El correo electrónico del usuario.
     * @param gender El género seleccionado por el usuario.
     * @param password La contraseña ingresada por el usuario.
     * @param confirmPassword La confirmación de la contraseña.
     * @return Devuelve true si todos los campos son válidos, de lo contrario false.
     */
    private fun validateInputs(
        name: String,
        email: String,
        gender: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true

        // Validación de nombre
        if (name.isEmpty() || name.length < 2 || !name.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+\$"))) {
            nameError.text = "Nombre no válido. Solo letras y espacios."
            validarCampo(findViewById(R.id.nameEditText), false)
            isValid = false
        } else {
            validarCampo(findViewById(R.id.nameEditText), true)
        }

        // Validación de correo
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError.text = "Correo electrónico no válido."
            validarCampo(findViewById(R.id.emailEditText), false)
            isValid = false
        } else {
            validarCampo(findViewById(R.id.emailEditText), true)
        }

        // Validación de género
        if (gender == "Selecciona tu sexo") {
            showToast("Selecciona un sexo.")
            isValid = false
        }

        // Validación de contraseña
        if (password.isEmpty() || password.length < 6) {
            passError.text = "La contraseña debe tener al menos 6 caracteres."
            validarCampo(findViewById(R.id.passwordEditText), false)
            isValid = false
        } else {
            validarCampo(findViewById(R.id.passwordEditText), true)
        }

        // Validación de confirmación de contraseña
        if (confirmPassword.isEmpty() || confirmPassword != password) {
            confirmPassError.text = "Las contraseñas no coinciden."
            validarCampo(findViewById(R.id.confirmPasswordEditText), false)
            isValid = false
        } else {
            validarCampo(findViewById(R.id.confirmPasswordEditText), true)
        }

        if (!isValid) {
            showToast("Error en los campos.")
        }
        return isValid
    }

    /**
     * Registra al usuario en Firebase con los datos proporcionados.
     *
     * Si el registro es exitoso, se guarda el nombre, correo y género del usuario en Firestore
     * y se redirige a la pantalla de inicio de sesión.
     *
     * @param name El nombre completo del usuario.
     * @param email El correo electrónico del usuario.
     * @param gender El género seleccionado por el usuario.
     * @param password La contraseña seleccionada por el usuario.
     */
    private fun registerUser(name: String, email: String, gender: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val userMap = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "gender" to gender
                    )

                    userId?.let { uid ->
                        db.collection("users").document(uid).set(userMap)
                            .addOnSuccessListener {
                                showToast("Registro exitoso")
                                goToLogin()
                            }
                            .addOnFailureListener {
                                showToast("Error al guardar datos: ${it.message}")
                            }
                    }
                } else {
                    showToast("Error en el registro: ${it.exception?.message}")
                }
            }
    }

    /**
     * Muestra un mensaje en forma de toast al usuario.
     *
     * @param message El mensaje a mostrar en el toast.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Cambia el fondo del campo según si es válido o no.
     *
     * @param editText El campo de texto que se está validando.
     * @param isValid Indica si el campo es válido o no.
     */
    private fun validarCampo(editText: EditText, isValid: Boolean) {
        if (isValid) {
            editText.background = ContextCompat.getDrawable(this, R.drawable.edittext_bg)
        } else {
            editText.background = ContextCompat.getDrawable(this, R.drawable.edittext_error)
        }
    }

    /**
     * Redirige al usuario a la pantalla de inicio de sesión.
     */
    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
