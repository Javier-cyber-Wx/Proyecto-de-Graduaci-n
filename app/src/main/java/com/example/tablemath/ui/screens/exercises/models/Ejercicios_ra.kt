package com.example.tablemath.ui.screens.exercises.models

import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import kotlin.math.max

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
            val context = LocalContext.current

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
                        val mediaPlayer = MediaPlayer.create(context, com.example.tablemath.R.raw.correcto)
                        mediaPlayer.start()
                        // Esperar a que termine el sonido antes de liberar recursos
                        mediaPlayer.setOnCompletionListener {
                            it.release()
                        }
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

        // Animación de aparición de intersecciones
        LaunchedEffect(a, b) {
            userInput = ""
            message = ""
            correct = false
            visibleCount = 0
            for (i in 1..totalIntersections) {
                delay(150)
                visibleCount = i
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val maxLines = max(a, b)
            val canvasHeight = (maxLines * 40).dp
            val scale = remember { mutableStateOf(1f) }
            val offset = remember { mutableStateOf(Offset.Zero) }

            val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
                scale.value = (scale.value * zoomChange).coerceIn(0.5f, 3f) // límites de zoom
                offset.value += panChange
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(canvasHeight)
                    .transformable(state = transformableState)
                    .background(Color.White)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color.LightGray)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale.value,
                            scaleY = scale.value,
                            translationX = offset.value.x,
                            translationY = offset.value.y
                        )
                ) {
                    val topMargin = 40f
                    val bottomMargin = 40f
                    val pointRadius = 5f

                    val availableHeight = size.height - topMargin - bottomMargin

                    val spacingA = (availableHeight - 2 * pointRadius) / a
                    val spacingB = (availableHeight - 2 * pointRadius) / b

                    val lineA = mutableListOf<Pair<Offset, Offset>>()
                    val lineB = mutableListOf<Pair<Offset, Offset>>()

                    // Líneas ↘ (azules)
                    for (i in 1..a) {
                        val y = topMargin + i * spacingA
                        val start = Offset(0f, y)
                        val end = Offset(size.width, y - size.width)
                        lineA.add(start to end)
                        drawLine(color = Color(0xFF2196F3), start = start, end = end, strokeWidth = 4f)
                    }

                    // Líneas ↙ (rojas)
                    for (j in 1..b) {
                        val y = topMargin + j * spacingB
                        val start = Offset(size.width, y)
                        val end = Offset(0f, y - size.width)
                        lineB.add(start to end)
                        drawLine(color = Color(0xFFF44336), start = start, end = end, strokeWidth = 4f)
                    }

                    // Puntos de intersección animados
                    var index = 0
                    for ((startA, endA) in lineA) {
                        for ((startB, endB) in lineB) {
                            val intersection = getLineIntersection(startA, endA, startB, endB)
                            if (intersection != null && index < visibleCount) {
                                drawCircle(color = Color.Black, radius = pointRadius, center = intersection)
                            }
                            index++
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- Barra de progreso debajo del canvas ---
            LinearProgressIndicator(
                progress = progreso,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = Color(0xFF6C1AEF),
                trackColor = Color(0xFFE1BEE7)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Título debajo de la barra ---
            Text(
                text = "Método Japonés: $a × $b",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6C1AEF)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Input y botones ---
            if (!correct) {
                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    label = { Text("¿Cuántos cruces hay?") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))
                val context = LocalContext.current
                Button(
                    onClick = {
                        if (userInput.toIntOrNull() == totalIntersections) {
                            message = "🎉 ¡Correcto!"
                            correct = true
                            val aciertoPlayer = MediaPlayer.create(context, com.example.tablemath.R.raw.finalizada)
                            aciertoPlayer.start()
                            // Esperar a que termine el sonido antes de liberar recursos
                            aciertoPlayer.setOnCompletionListener {
                                it.release()
                            }
                        } else {
                            message = "❌ Intenta de nuevo"
                            val errorPlayer = MediaPlayer.create(context, com.example.tablemath.R.raw.error)
                            errorPlayer.start()
                            // Esperar a que termine el sonido antes de liberar recursos
                            errorPlayer.setOnCompletionListener {
                                it.release()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C1AEF))
                ) {
                    Text("Comprobar", color = Color.White)
                }
            } else {
                val context = LocalContext.current
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
    // --- Función auxiliar para calcular intersecciones ---
    fun getLineIntersection(
        p1: Offset, p2: Offset,
        p3: Offset, p4: Offset
    ): Offset? {
        val denom = (p1.x - p2.x) * (p3.y - p4.y) - (p1.y - p2.y) * (p3.x - p4.x)
        if (denom == 0f) return null
        val x = ((p1.x * p2.y - p1.y * p2.x) * (p3.x - p4.x) -
                (p1.x - p2.x) * (p3.x * p4.y - p3.y * p4.x)) / denom
        val y = ((p1.x * p2.y - p1.y * p2.x) * (p3.y - p4.y) -
                (p1.y - p2.y) * (p3.x * p4.y - p3.y * p4.x)) / denom
        return Offset(x, y)
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
            val context = LocalContext.current

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
                        val mediaPlayer = MediaPlayer.create(context, com.example.tablemath.R.raw.correcto)
                        mediaPlayer.start()
                        // Esperar a que termine el sonido antes de liberar recursos
                        mediaPlayer.setOnCompletionListener {
                            it.release()
                        }
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
        val aDigits = a.toString().map { it.toString().toInt() }
        val bDigits = b.toString().map { it.toString().toInt() }

        val totalCells = aDigits.size * bDigits.size
        val decenas = Array(bDigits.size) { IntArray(aDigits.size) }
        val unidades = Array(bDigits.size) { IntArray(aDigits.size) }

        var showCells by remember { mutableStateOf(0) }
        var showDiagonals by remember { mutableStateOf(false) }
        var highlightedDiagonal by remember { mutableStateOf(-1) }
        var resultadoInput by remember { mutableStateOf("") }
        var correcto by remember { mutableStateOf(false) }
        var mensaje by remember { mutableStateOf("") }
        var resultadoFinal by remember { mutableStateOf("") }
        var celdaActiva by remember { mutableStateOf<Pair<Int, Int>?>(null) }

        // Animación paso a paso
        LaunchedEffect(a, b) {
            showCells = 0
            showDiagonals = false
            highlightedDiagonal = -1
            resultadoInput = ""
            correcto = false
            mensaje = ""
            resultadoFinal = ""
            celdaActiva = null

            for (i in 1..totalCells) {
                delay(200)
                showCells = i
            }

            delay(300)
            showDiagonals = true

            val totalColumns = aDigits.size
            val totalRows = bDigits.size
            val diagonalCount = totalColumns + totalRows
            val diagonals = IntArray(diagonalCount) { 0 }

            for (i in 0 until totalRows) {
                for (j in 0 until totalColumns) {
                    val multiplicando = aDigits[j]
                    val multiplicador = bDigits[i]
                    val producto = multiplicando * multiplicador
                    val decena = producto / 10
                    val unidad = producto % 10

                    decenas[i][j] = decena
                    unidades[i][j] = unidad

                    val diagonalIndex = i + j
                    diagonals[diagonalIndex] += unidad
                    if (diagonalIndex - 1 >= 0) {
                        diagonals[diagonalIndex - 1] += decena
                    }
                }
            }

            for (d in diagonals.indices) {
                highlightedDiagonal = d
                delay(500)
            }
            highlightedDiagonal = -1

            resultadoFinal = diagonals.reversed().fold(0) { acc, value -> acc * 10 + value }.toString()
        }

        val progreso by remember { derivedStateOf { showCells.toFloat() / totalCells } }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Método Árabe (Celosía): $a × $b", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            LinearProgressIndicator(
                progress = progreso.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFF6C1AEF),
                trackColor = Color.LightGray
            )

            Column {
                // Fila superior con los dígitos de 'a'
                Row(modifier = Modifier.padding(start = 60.dp)) {
                    for (digit in aDigits) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(Color(0xFFE0F7FA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = digit.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00796B)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Cuadrícula con columna izquierda
                var cellIndex = 0
                for (i in bDigits.indices) {
                    Row {
                        // Dígito de 'b' al inicio de la fila
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(Color(0xFFE0F7FA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = bDigits[i].toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00796B)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))

                        // Celdas de la cuadrícula
                        for (j in aDigits.indices) {
                            val multiplicando = aDigits[j]
                            val multiplicador = bDigits[i]
                            val producto = multiplicando * multiplicador
                            val decena = producto / 10
                            val unidad = producto % 10
                            val diagonalIndex = i + j
                            val isHighlighted = highlightedDiagonal == diagonalIndex || highlightedDiagonal == diagonalIndex - 1
                            val diagonalColor = if (isHighlighted) Color(0xFFFFC107) else Color.Gray

                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .border(2.dp, Color.Black)
                                    .background(Color(0xFFF3F3F3))
                                    .clickable { celdaActiva = i to j },
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawLine(
                                        color = diagonalColor,
                                        start = Offset(0f, 0f),
                                        end = Offset(size.width, size.height),
                                        strokeWidth = 2f
                                    )
                                }

                                if (cellIndex < showCells) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        Text(
                                            text = decena.toString(),
                                            color = Color(0xFF1976D2),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            modifier = Modifier.align(Alignment.TopStart).padding(2.dp)
                                        )
                                        Text(
                                            text = unidad.toString(),
                                            color = Color(0xFFD32F2F),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            modifier = Modifier.align(Alignment.BottomEnd).padding(2.dp)
                                        )
                                    }
                                }

                                cellIndex++
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            // Tarjeta explicativa por celda
            celdaActiva?.let { (i, j) ->
                val multiplicando = aDigits[j]
                val multiplicador = bDigits[i]
                val producto = multiplicando * multiplicador
                val decena = producto / 10
                val unidad = producto % 10
                val diagonalActual = i + j
                val diagonalAnterior = diagonalActual - 1

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("📦 Celda seleccionada: fila $i, columna $j", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text("🔢 Cálculo: $multiplicando × $multiplicador = $producto")
                        Text("🔷 Decena: $decena → va a la diagonal $diagonalAnterior")
                        Text("🔴 Unidad: $unidad → va a la diagonal $diagonalActual")
                        Spacer(Modifier.height(8.dp))
                        Text("🧠 Esta celda contribuye al resultado final sumando en dos diagonales.")
                    }
                }
            }

            // Explicación general
            if (showDiagonals) {
                Text("🔍 Observa cómo cada diagonal recoge decenas y unidades.", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF6C1AEF))
                Text("Las decenas van a la diagonal anterior, las unidades a la actual.", fontSize = 14.sp, color = Color.DarkGray)
            }

            // Input del usuario
            if (showDiagonals && !correcto) {
                OutlinedTextField(
                    value = resultadoInput,
                    onValueChange = { resultadoInput = it },
                    label = { Text("Escribe el resultado final") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                val context = LocalContext.current
                Button(
                    onClick = {
                        if (resultadoInput.toIntOrNull() == a * b) {
                            correcto = true
                            mensaje = "🎉 ¡Correcto! $a × $b = ${a * b}"
                            val aciertoPlayer = MediaPlayer.create(context, com.example.tablemath.R.raw.finalizada)
                            aciertoPlayer.start()
                            // Esperar a que termine el sonido antes de liberar recursos
                            aciertoPlayer.setOnCompletionListener {
                                it.release()
                            }
                        } else {
                            mensaje = "❌ Intenta de nuevo"
                            val errorPlayer = MediaPlayer.create(context, com.example.tablemath.R.raw.error)
                            errorPlayer.start()
                            // Esperar a que termine el sonido antes de liberar recursos
                            errorPlayer.setOnCompletionListener {
                                it.release()
                            }
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