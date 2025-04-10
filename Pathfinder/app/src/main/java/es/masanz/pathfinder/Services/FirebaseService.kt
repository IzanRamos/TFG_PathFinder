package es.masanz.pathfinder.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Servicio que centraliza todas las operaciones relacionadas con Firebase,
 * como autenticación y almacenamiento de datos en Firestore.
 *
 * Esta clase permite mantener una estructura modular y desacoplada
 * respecto a las actividades o fragmentos que consumen estos servicios.
 *
 * @author Izan
 */
class FirebaseService {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Registra un nuevo usuario en Firebase Authentication y guarda sus datos básicos en Firestore.
     *
     * @param name El nombre del usuario.
     * @param email El correo electrónico del usuario.
     * @param gender El género del usuario.
     * @param password La contraseña del usuario.
     * @param onSuccess Función de callback que se ejecuta si el registro fue exitoso.
     * @param onError Función de callback que se ejecuta si ocurre un error. Recibe un mensaje con la descripción del fallo.
     */
    fun registerUser(
        name: String,
        email: String,
        gender: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val userMap = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "gender" to gender
                    )

                    userId?.let { uid ->
                        db.collection("users").document(uid).set(userMap)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { exception ->
                                onError("Error al guardar datos en Firestore: ${exception.message}")
                            }
                    } ?: onError("No se pudo obtener el UID del usuario.")
                } else {
                    onError("Error al registrar usuario: ${task.exception?.message}")
                }
            }
    }
}
