package com.example.tablemath.ui.screens.exercises.models

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay

class Ejercicios_ra {
    @Composable
    fun JapaneseExerciseScreen(
        tabla: Int = 2,
        estudianteId: String,
        onFinish: () -> Unit
    ) {
        val db = Firebase.firestore

        var ejercicios by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
        var currentIndex by remember { mutableStateOf(0) }
        var cargando by remember { mutableStateOf(true) }
        var mensaje by remember { mutableStateOf("") }

        // Datos del ejercicio actual
        var a by remember { mutableStateOf(0) }
        var b by remember { mutableStateOf(0) }

        // --- Cargar ejercicios desde Firestore ---
        LaunchedEffect(Unit) {
            db.collection("ejercicios")
                .document("japones")
                .get()
                .addOnSuccessListener { doc ->
                    val ejerciciosArray = doc.get("ejercicios") as? List<Map<String, Any>>
                    if (ejerciciosArray != null) {
                        val filtrados = ejerciciosArray.filter { (it["tabla"] as? Long)?.toInt() == tabla }
                        if (filtrados.isNotEmpty()) {
                            ejercicios = filtrados
                            val primer = filtrados[0]
                            a = (primer["a"] as Long).toInt()
                            b = (primer["b"] as Long).toInt()
                        } else {
                            mensaje = "No hay ejercicios para la tabla $tabla"
                        }
                    } else {
                        mensaje = "No se encontraron ejercicios en Firestore"
                    }
                    cargando = false
                }
                .addOnFailureListener { e ->
                    mensaje = "Error al cargar: ${e.message}"
                    cargando = false
                }
        }

        if (cargando) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else if (ejercicios.isEmpty()) {
            Text(text = mensaje, color = Color.Red, fontSize = 18.sp)
        } else {
            val progreso = (currentIndex + 1).toFloat() / ejercicios.size.toFloat()

            JapaneseExercise(
                a = a,
                b = b,
                progreso = progreso,
                onFinish = {
                    // Pasar al siguiente ejercicio
                    if (currentIndex + 1 < ejercicios.size) {
                        currentIndex++
                        val siguiente = ejercicios[currentIndex]
                        a = (siguiente["a"] as Long).toInt()
                        b = (siguiente["b"] as Long).toInt()
                    } else {
                        onFinish()
                    }
                }
            )
        }
    }
    @Composable
    fun JapaneseExercise(
        a: Int,
        b: Int,
        progreso: Float,
        onFinish: () -> Unit
    ) {
        var userInput by remember { mutableStateOf("") }
        var message by remember { mutableStateOf("") }
        var correct by remember { mutableStateOf(false) }
        var visibleCount by remember { mutableStateOf(0) }

        val totalIntersections = a * b
        LaunchedEffect(a, b) {
            userInput = ""
            message = ""
            correct = false
            visibleCount = 0
            for (i in 1..totalIntersections) {
                delay(200)
                visibleCount = i
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Barra de progreso estilo Duolingo ---
            LinearProgressIndicator(
                progress = progreso,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = Color(0xFF6C1AEF),
                trackColor = Color(0xFFE1BEE7)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Método Japonés: $a × $b",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6C1AEF)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- Grilla de cruces ✖ ---
            Column {
                for (i in 0 until a) {
                    Row(horizontalArrangement = Arrangement.Center) {
                        for (j in 0 until b) {
                            val index = i * b + j
                            AnimatedVisibility(visible = index < visibleCount) {
                                Text(
                                    text = "✖",
                                    fontSize = 32.sp,
                                    modifier = Modifier.padding(4.dp),
                                    color = Color(0xFFFF4081)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!correct) {
                // --- Input de respuesta ---
                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    label = { Text("¿Cuántos cruces hay?") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (userInput.toIntOrNull() == totalIntersections) {
                            message = "🎉 ¡Correcto!"
                            correct = true
                        } else {
                            message = "❌ Intenta de nuevo"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C1AEF))
                ) {
                    Text("Comprobar", color = Color.White)
                }
            } else {
                // --- Botón para continuar ---
                Button(
                    onClick = { onFinish() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Siguiente ➡", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (correct) Color(0xFF4CAF50) else Color.Red
                )
            }
        }
    }
    @Composable
    fun ArabicExerciseScreen(
        tabla: Int,
        estudianteId: String,
        onFinish: () -> Unit = {}
    ) {
        val db = Firebase.firestore

        var ejercicios by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
        var currentIndex by remember { mutableStateOf(0) }
        var cargando by remember { mutableStateOf(true) }
        var mensaje by remember { mutableStateOf("") }

        // Datos actuales
        var a by remember { mutableStateOf(0) }
        var b by remember { mutableStateOf(0) }

        // --- Cargar ejercicios desde Firestore ---
        LaunchedEffect(Unit) {
            db.collection("ejercicios")
                .document("arabe")
                .get()
                .addOnSuccessListener { doc ->
                    val ejerciciosArray = doc.get("ejercicios") as? List<Map<String, Any>>
                    if (ejerciciosArray != null) {
                        val filtrados = ejerciciosArray.filter { (it["tabla"] as? Long)?.toInt() == tabla }
                        if (filtrados.isNotEmpty()) {
                            ejercicios = filtrados
                            val primero = filtrados[0]
                            a = (primero["a"] as Long).toInt()
                            b = (primero["b"] as Long).toInt()
                        } else {
                            mensaje = "No hay ejercicios para la tabla $tabla"
                        }
                    } else {
                        mensaje = "No se encontraron ejercicios en Firestore"
                    }
                    cargando = false
                }
                .addOnFailureListener { e ->
                    mensaje = "Error al cargar: ${e.message}"
                    cargando = false
                }
        }

        if (cargando) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else if (ejercicios.isEmpty()) {
            Text(text = mensaje, color = Color.Red, fontSize = 18.sp)
        } else {
            // --- Ejercicio paso a paso ---
            ArabicStepByStep(
                a = a,
                b = b,
                onFinish = {
                    // Pasar al siguiente ejercicio
                    if (currentIndex + 1 < ejercicios.size) {
                        currentIndex++
                        val siguiente = ejercicios[currentIndex]
                        a = (siguiente["a"] as Long).toInt()
                        b = (siguiente["b"] as Long).toInt()
                    } else {
                        // Todos los ejercicios completados
                        onFinish()
                    }
                }
            )
        }
    }
    @Composable
    fun ArabicStepByStep(
        a: Int,
        b: Int,
        onFinish: () -> Unit
    ) {
        val totalCells = a * b
        var showCells by remember { mutableStateOf(0) }
        var showDiagonals by remember { mutableStateOf(false) }
        var resultadoInput by remember { mutableStateOf("") }
        var correcto by remember { mutableStateOf(false) }
        var mensaje by remember { mutableStateOf("") }

        // Animación paso a paso
        LaunchedEffect(a, b) {
            showCells = 0
            showDiagonals = false
            resultadoInput = ""
            correcto = false
            mensaje = ""

            // Mostrar cada celda
            for (i in 1..totalCells) {
                delay(200)
                showCells = i
            }

            delay(300)
            showDiagonals = true
        }

        val producto = a * b
        val progreso by remember { derivedStateOf { (showCells.toFloat() / totalCells) } }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Método Árabe (Celosía): $a × $b",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            // Barra de progreso
            LinearProgressIndicator(
                progress = progreso.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFF6C1AEF),
                trackColor = Color.LightGray
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Cuadrícula real
            Column {
                var cellIndex = 0
                for (i in 0 until a) {
                    Row {
                        for (j in 0 until b) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .border(1.dp, Color.Black),
                                contentAlignment = Alignment.Center
                            ) {
                                // Dibujar diagonal
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawLine(
                                        color = Color.Gray,
                                        start = Offset(0f, 0f),
                                        end = Offset(size.width, size.height),
                                        strokeWidth = 2f
                                    )
                                }

                                if (cellIndex < showCells) {
                                    // Producto en la celda
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        // Decena
                                        Text(
                                            text = "0",
                                            color = Color.Blue,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            modifier = Modifier.align(Alignment.TopStart).padding(2.dp)
                                        )
                                        // Unidad
                                        Text(
                                            text = "1",
                                            color = Color.Red,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            modifier = Modifier.align(Alignment.BottomEnd).padding(2.dp)
                                        )
                                    }
                                }

                                cellIndex++
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // Suma de diagonales
            if (showDiagonals) {
                Text(
                    text = "Suma de diagonales = $producto",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }

            // Input del resultado final
            if (showDiagonals && !correcto) {
                OutlinedTextField(
                    value = resultadoInput,
                    onValueChange = { resultadoInput = it },
                    label = { Text("Escribe el resultado final") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (resultadoInput.toIntOrNull() == producto) {
                            correcto = true
                            mensaje = "🎉 ¡Correcto! $a × $b = $producto"
                        } else {
                            mensaje = "❌ Intenta de nuevo"
                        }
                    },
                    enabled = !correcto,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B))
                ) {
                    Text("Verificar", color = Color.White)
                }
            }

            if (correcto) {
                Button(
                    onClick = { onFinish() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("➡️ Siguiente", color = Color.White)
                }
            }

            if (mensaje.isNotEmpty()) {
                Text(
                    text = mensaje,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (correcto) Color(0xFF4CAF50) else Color.Red
                )
            }
        }
    }
}