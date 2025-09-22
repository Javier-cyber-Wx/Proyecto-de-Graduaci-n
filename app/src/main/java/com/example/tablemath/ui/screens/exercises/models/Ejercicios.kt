package com.example.tablemath.ui.screens.exercises.models

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tablemath.data.model.Estudiante
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class Ejercicios {
    @Composable
    fun ExerciseScreen(
        metodo: String = "clasico",
        tabla: Int = 1,
        estudianteId: String,
        onFinish: () -> Unit
    ) {
        val db = Firebase.firestore
        var ejercicios by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
        var currentIndex by remember { mutableStateOf(0) }
        var respuestaUsuario by remember { mutableStateOf("") }
        var mensaje by remember { mutableStateOf("") }

        // Consumir ejercicios desde Firebase
        LaunchedEffect(metodo, tabla) {
            db.collection("ejercicios")
                .document("clasico") // ID del documento igual al nombre del método
                .get()
                .addOnSuccessListener { doc ->
                    val ejerciciosArray = doc.get("ejercicios") as? List<Map<String, Any>>
                    if (ejerciciosArray != null) {
                        // Filtrar por la tabla seleccionada
                        ejercicios = ejerciciosArray.filter {
                            (it["tabla"] as? Long)?.toInt() == tabla
                        }
                        if (ejercicios.isEmpty()) {
                            mensaje = "No se encontraron ejercicios para la tabla $tabla"
                        }
                    } else {
                        mensaje = "No se encontraron ejercicios para $metodo"
                    }
                }
                .addOnFailureListener { e ->
                    mensaje = "Error al cargar ejercicios: ${e.message}"
                }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (ejercicios.isNotEmpty()) {
                val ejercicioActual = ejercicios[currentIndex]
                Text(
                    text = ejercicioActual["enunciado"] as? String ?: "Error",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = respuestaUsuario,
                    onValueChange = { respuestaUsuario = it },
                    label = { Text("Escribe tu respuesta") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    val respuestaCorrecta = (ejercicioActual["respuesta_correcta"] as? Long)?.toInt()
                    if (respuestaUsuario.toIntOrNull() == respuestaCorrecta) {
                        mensaje = "¡Correcto!, felicidades 🎉"

                        // Guardar progreso en Firebase
                        val progreso = hashMapOf(
                            "id_alumno" to estudianteId,
                            "id_ejercicio" to "${metodo}_tabla${tabla}_$currentIndex",
                            "puntaje" to 1,
                            "fecha" to System.currentTimeMillis()
                        )
                        db.collection("progreso").add(progreso)

                        if (currentIndex < ejercicios.size - 1) {
                            currentIndex++
                            respuestaUsuario = ""
                        } else {
                            mensaje = "¡Terminaste la tabla $tabla!"
                            onFinish()
                        }
                    } else {
                        mensaje = "Intenta de nuevo, ¡tú puedes!"
                    }
                }) {
                    Text("Verificar")
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = mensaje, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            } else {
                // Mostrar mensaje de carga o error
                if (mensaje.isEmpty()) {
                    CircularProgressIndicator()
                } else {
                    Text(text = mensaje, color = Color.Red, fontSize = 18.sp)
                }
            }
        }
    }
}


