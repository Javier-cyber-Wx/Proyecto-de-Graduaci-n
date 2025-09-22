package com.example.tablemath.domain

import com.example.tablemath.data.model.Estudiante
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import android.util.Log

class EstudianteRepository{
    private val db = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val estudiantesCollection = db.collection("estudiantes")
    fun registrarEstudiante(estudiante: Estudiante, onResult: (Boolean, String?) -> Unit){
        val email_ficticio = "${estudiante.codigoEstudiante}@tablemath.com"
        val pin = estudiante.codigoEstudiante

        auth.createUserWithEmailAndPassword(email_ficticio, pin)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: return@addOnSuccessListener
                estudiantesCollection.document(uid)
                    .set(estudiante)
                    .addOnSuccessListener {
                        Log.d("Registro", "Estudiante registrado con UID: $uid")
                        onResult(true, null)
                    }
                    .addOnFailureListener { e ->
                        Log.e("EstudianteRepository", "Error al registrar estudiante en Firestore", e)
                        onResult(false, e.message ?: "Error desconocido")
                    }
            }
            .addOnFailureListener {
                Log.w("Registro", "Error al crear usuario", it)
                onResult(false, it.message ?: "Error desconocido")
            }
    }
    fun loginEstudiante(codigoEstudiante: String, onResult: (Boolean, String?, String?) -> Unit) {
        val email_ficticio = "${codigoEstudiante}@tablemath.com"
        val pin = codigoEstudiante // Usar el código del estudiante como PIN

        auth.signInWithEmailAndPassword(email_ficticio, pin)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid ?: return@addOnSuccessListener
                estudiantesCollection.document(uid)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val estudiante = document.toObject(Estudiante::class.java)
                            if (estudiante != null) {
                                Log.d("Login", "Estudiante encontrado: ${estudiante.nombre}")
                                onResult(true, null, uid)
                            } else {
                                Log.w("Login", "No se pudo convertir el documento a Estudiante")
                                onResult(false, "Datos del estudiante no encontrados", null)
                            }
                        } else {
                            Log.w("Login", "No se encontró el documento del estudiante")
                            onResult(false, "Estudiante no encontrado", null)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("EstudianteRepository", "Error al obtener datos del estudiante", e)
                        onResult(false, e.message ?: "Error desconocido", null)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("Login", "Error al iniciar sesión", e)
                onResult(false, e.message ?: "Error desconocido", null)
            }
    }
}

