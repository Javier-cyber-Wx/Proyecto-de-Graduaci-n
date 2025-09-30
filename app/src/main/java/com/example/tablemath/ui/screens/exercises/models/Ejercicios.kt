package com.example.tablemath.ui.screens.exercises.models

import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.material3.TextField
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.toInt
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.ui.draw.clip
import androidx.media3.common.Player
import com.example.tablemath.R
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.max

class Ejercicios {
    @Composable
    fun ExerciseScreen(
        tabla: Int = 1,
        metodo: String,
        estudianteId: String,
        onFinish: () -> Unit
    ) {
        val db = Firebase.firestore
        var ejercicios by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
        var currentIndex by remember { mutableStateOf(0) }
        var respuestaUsuario by remember { mutableStateOf("") }
        var mensaje by remember { mutableStateOf("") }
        var cargando by remember { mutableStateOf(true) }
        var respuestaCorrecta by remember { mutableStateOf(false) }
        val colorPrimario = Color(0xFF6C1AEF)
        val colorCorrecto = Color(0xFF4CAF50)
        val colorError = Color(0xFFE57373)
        val colorFeedback = Color(0xFFFFC107)
        // Animación del color del feedback
        val mensajeColor by animateColorAsState(
            targetValue = when {
                respuestaCorrecta -> colorCorrecto
                mensaje.contains("Intenta") -> colorError
                else -> colorFeedback
            },
            animationSpec = tween(500)
        )
        LaunchedEffect(Unit) {
            db.collection("ejercicios")
                .document("clasico")
                .get()
                .addOnSuccessListener { doc ->
                    val ejerciciosArray = doc.get("ejercicios") as? List<Map<String, Any>>
                    if (ejerciciosArray != null) {
                        val filtrados = ejerciciosArray.filter { ejercicio ->
                            val enunciado = ejercicio["enunciado"] as? String ?: ""
                            enunciado.startsWith("$tabla x")
                        }
                        if (filtrados.isNotEmpty()) {
                            ejercicios = filtrados
                        } else {
                            mensaje = "No se encontraron ejercicios para la tabla $tabla"
                        }
                    } else {
                        mensaje = "No se encontraron ejercicios para clásico"
                    }
                    cargando = false
                }
                .addOnFailureListener { e ->
                    mensaje = "Error al cargar ejercicios: ${e.message}"
                    cargando = false
                }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (cargando) {
                CircularProgressIndicator(color = colorPrimario)
            } else if (ejercicios.isEmpty()) {
                Text(text = mensaje, color = Color.Red, fontSize = 18.sp)
            } else {
                val ejercicioActual = ejercicios[currentIndex]
                val enunciado = ejercicioActual["enunciado"] as? String ?: "Error"
                val respuestaCorrectaEsperada = (ejercicioActual["respuesta_correcta"] as? Long)?.toInt()

                // 🔹 Enunciado
                Text(
                    text = enunciado,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorPrimario
                )
                Spacer(modifier = Modifier.height(20.dp))

                // 🔹 Caja de respuesta
                OutlinedTextField(
                    value = respuestaUsuario,
                    onValueChange = { respuestaUsuario = it },
                    label = { Text("Tu respuesta ✍️") },
                    singleLine = true,
                    enabled = !respuestaCorrecta,
                    textStyle = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Medium)
                )
                Spacer(modifier = Modifier.height(20.dp))
                // 🔹 Botón verificar
                val context = LocalContext.current
                Button(
                    onClick = {
                        if (respuestaUsuario.toIntOrNull() == respuestaCorrectaEsperada) {
                            mensaje = "🎉 ¡Correcto! Muy bien"
                            respuestaCorrecta = true
                            val aciertoPlayer = MediaPlayer.create(context, R.raw.finalizada)
                            aciertoPlayer.start()
                            // Esperar a que termine el sonido antes de liberar recursos
                            aciertoPlayer.setOnCompletionListener {
                                it.release()
                            }

                            // Guardar progreso
                            val progreso = hashMapOf(
                                "id_alumno" to estudianteId,
                                "id_ejercicio" to "clasico_tabla${tabla}_$currentIndex",
                                "puntaje" to 1,
                                "fecha" to System.currentTimeMillis()
                            )
                            db.collection("progreso").add(progreso)
                        } else {
                            mensaje = "❌ Intenta de nuevo 😅"
                            val errorPlayer = MediaPlayer.create(context, R.raw.error)
                            errorPlayer.start()
                            // Esperar a que termine el sonido antes de liberar recursos
                            errorPlayer.setOnCompletionListener {
                                it.release()
                            }
                        }
                    },
                    enabled = !respuestaCorrecta,
                    colors = ButtonDefaults.buttonColors(containerColor = colorPrimario),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Text("Verificar", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
                // 🔹 Feedback
                if (mensaje.isNotEmpty()) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = mensajeColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = mensaje,
                            modifier = Modifier.padding(16.dp),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                if (respuestaCorrecta) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            if (currentIndex < ejercicios.size - 1) {
                                currentIndex++
                                respuestaUsuario = ""
                                mensaje = ""
                                respuestaCorrecta = false
                            } else {
                                val mediaPlayer = MediaPlayer.create(context, R.raw.correcto)
                                mediaPlayer.start()
                                // Esperar a que termine el sonido antes de liberar recursos
                                mediaPlayer.setOnCompletionListener {
                                    it.release()
                                }
                                mensaje = "🏆 ¡Terminaste la tabla $tabla!"
                                onFinish()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colorCorrecto),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                    ) {
                        Text("➡️ Siguiente", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
    @Composable
    fun ExerciseScreenRuso(
        tabla: Int = 2,
        estudianteId: String,
        onFinish: () -> Unit
    ) {
        val db = Firebase.firestore
        // --- Estados ---
        var ejercicios by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
        var currentIndex by remember { mutableStateOf(0) }
        var cargando by remember { mutableStateOf(true) }
        var mensaje by remember { mutableStateOf("") }

        var aActual by remember { mutableStateOf(0) }
        var bActual by remember { mutableStateOf(0) }
        var pasosCompletados by remember { mutableStateOf(listOf<Pair<Int, Int>>()) }
        var resultadoFinal by remember { mutableStateOf(0) }

        var mitadInput by remember { mutableStateOf("") }
        var dobleInput by remember { mutableStateOf("") }
        var resultadoInput by remember { mutableStateOf("") }

        var esperandoResultadoFinal by remember { mutableStateOf(false) }
        var respuestaCorrecta by remember { mutableStateOf(false) }

        val colorPrimario = Color(0xFF1E88E5)
        val colorCorrecto = Color(0xFF43A047)
        val colorError = Color.Red
        val colorFondo = Color.White
        val colorBoton = Color(0xFFFFC107)

        // --- Cargar ejercicios ---
        LaunchedEffect(Unit) {
            db.collection("ejercicios")
                .document("ruso")
                .get()
                .addOnSuccessListener { doc ->
                    val ejerciciosArray = doc.get("ejercicios") as? List<Map<String, Any>>
                    if (ejerciciosArray != null) {
                        val filtrados = ejerciciosArray.filter { (it["tabla"] as? Long)?.toInt() == tabla }
                        if (filtrados.isNotEmpty()) {
                            ejercicios = filtrados
                            val primerEjercicio = filtrados[0]
                            aActual = (primerEjercicio["a"] as Long).toInt()
                            bActual = (primerEjercicio["b"] as Long).toInt()
                            pasosCompletados = listOf()
                            resultadoFinal = 0
                        } else {
                            mensaje = "No se encontraron ejercicios para la tabla $tabla"
                        }
                    } else {
                        mensaje = "No se encontraron ejercicios en Firestore"
                    }
                    cargando = false
                }
                .addOnFailureListener { e ->
                    mensaje = "Error al cargar ejercicios: ${e.message}"
                    cargando = false
                }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorFondo)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (cargando) {
                CircularProgressIndicator(color = colorPrimario)
            } else if (ejercicios.isEmpty()) {
                Text(text = mensaje, color = Color.Red, fontSize = 18.sp)
            } else {
                val ejercicio = ejercicios[currentIndex]
                val aInicial = (ejercicio["a"] as Long).toInt()
                val bInicial = (ejercicio["b"] as Long).toInt()
                val enunciado = ejercicio["enunciado"] as? String ?: ""

                // --- Enunciado ---
                Text(
                    text = "Método Ruso: $enunciado",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorPrimario
                )
                Spacer(modifier = Modifier.height(16.dp))

                // --- Tabla de pasos ---
                pasosCompletados.forEach { (aPaso, bPaso) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = colorCorrecto)
                    ) {
                        Text(
                            text = "$aPaso → $bPaso",
                            modifier = Modifier.padding(12.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!esperandoResultadoFinal) {
                    // --- Inputs del usuario ---
                    OutlinedTextField(
                        value = mitadInput,
                        onValueChange = { mitadInput = it },
                        label = { Text("Mitad de $aActual") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = dobleInput,
                        onValueChange = { dobleInput = it },
                        label = { Text("Doble de $bActual") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Botón para avanzar paso ---
                    Button(
                        onClick = {
                            val mitadCorrecta = aActual / 2
                            val dobleCorrecto = bActual * 2

                            if (mitadInput.toIntOrNull() == mitadCorrecta &&
                                dobleInput.toIntOrNull() == dobleCorrecto
                            ) {
                                val esImpar = aActual % 2 != 0
                                if (esImpar) resultadoFinal += bActual

                                pasosCompletados = pasosCompletados + (aActual to bActual)
                                aActual = mitadCorrecta
                                bActual = dobleCorrecto

                                mitadInput = ""
                                dobleInput = ""

                                if (aActual == 0) {
                                    esperandoResultadoFinal = true
                                }
                            } else {
                                mensaje = "❌ Mitad o doble incorrectos"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colorPrimario),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Verificar paso", color = Color.Black, fontSize = 20.sp)
                    }
                } else {
                    // --- Resultado final ---
                    OutlinedTextField(
                        value = resultadoInput,
                        onValueChange = { resultadoInput = it },
                        label = { Text("Ingresa el resultado final") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    val context = LocalContext.current
                    Button(
                        onClick = {

                            if (resultadoInput.toIntOrNull() == resultadoFinal) {
                                mensaje = "🎉 ¡Correcto! Resultado = $resultadoFinal"
                                respuestaCorrecta = true
                                val aciertoPlayer = MediaPlayer.create(context, R.raw.finalizada)
                                aciertoPlayer.start()
                                // Esperar a que termine el sonido antes de liberar recursos
                                aciertoPlayer.setOnCompletionListener {
                                    it.release()
                                }
                                val progreso = hashMapOf(
                                    "id_alumno" to estudianteId,
                                    "id_ejercicio" to "ruso_tabla${tabla}_$currentIndex",
                                    "puntaje" to 1,
                                    "fecha" to System.currentTimeMillis()
                                )
                                db.collection("progreso").add(progreso)
                            } else {
                                mensaje = "❌ Resultado incorrecto"
                                val errorPlayer = MediaPlayer.create(context, R.raw.error)
                                errorPlayer.start()
                                // Esperar a que termine el sonido antes de liberar recursos
                                errorPlayer.setOnCompletionListener {
                                    it.release()
                                }
                            }
                        },
                        enabled = !respuestaCorrecta,
                        colors = ButtonDefaults.buttonColors(containerColor = colorBoton),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Verificar resultado", color = Color.Black, fontSize = 20.sp)
                    }

                    if (respuestaCorrecta) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (currentIndex < ejercicios.size - 1) {
                                    currentIndex++
                                    val siguiente = ejercicios[currentIndex]
                                    aActual = (siguiente["a"] as Long).toInt()
                                    bActual = (siguiente["b"] as Long).toInt()
                                    pasosCompletados = listOf()
                                    resultadoFinal = 0
                                    mitadInput = ""
                                    dobleInput = ""
                                    resultadoInput = ""
                                    esperandoResultadoFinal = false
                                    respuestaCorrecta = false
                                    mensaje = ""
                                } else {
                                    val mediaPlayer = MediaPlayer.create(context, R.raw.correcto)
                                    mediaPlayer.start()
                                    // Esperar a que termine el sonido antes de liberar recursos
                                    mediaPlayer.setOnCompletionListener {
                                        it.release()
                                    }
                                    mensaje = "🏆 ¡Terminaste la tabla $tabla!"
                                    onFinish()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colorCorrecto),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Siguiente ejercicio", color = Color.Black, fontSize = 20.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (mensaje.isNotEmpty()) {
                    Text(
                        text = mensaje,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (respuestaCorrecta) colorCorrecto else colorError
                    )
                }
            }
        }
    }
}