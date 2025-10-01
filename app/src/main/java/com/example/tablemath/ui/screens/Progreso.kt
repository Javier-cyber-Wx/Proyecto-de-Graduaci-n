package com.example.tablemath.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tablemath.ui.screens.ui.theme.TableMathTheme
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Progreso : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TableMathTheme {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Barra superior personalizada
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF6C1AEF))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { finish() }) {
                                Icon(
                                    Icons.Default.ArrowBack, 
                                    contentDescription = "Volver",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "📈 Mi Progreso", 
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                        }
                    }
                    
                    // Contenido principal
                    val estudianteId = intent.getStringExtra("estudianteId") ?: ""
                    ProgresoScreen(
                        estudianteId = estudianteId,
                        modifier = Modifier.fillMaxSize()
                )
                }
            }
        }
    }
}

@Composable
fun ProgresoScreen(
    estudianteId: String,
    modifier: Modifier = Modifier
) {
    val db = FirebaseFirestore.getInstance()
    var ejercicios by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(estudianteId) {
        if (estudianteId.isNotEmpty()) {
        db.collection("progreso")
            .whereEqualTo("id_alumno", estudianteId)
            .get()
                .addOnSuccessListener { result ->
                    android.util.Log.d("Progreso", "Progreso cargado: ${result.documents.size} documentos")
                    // Ordenar por fecha en el cliente (más recientes primero)
                    val ejerciciosOrdenados = result.documents
                        .mapNotNull { it.data }
                        .sortedByDescending { it["fecha"] as? Long ?: 0L }
                    ejercicios = ejerciciosOrdenados
                    cargando = false
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("Progreso", "Error al cargar progreso", e)
                    error = "Error al cargar progreso: ${e.message}"
                    cargando = false
                }
        } else {
            error = "ID de estudiante no válido"
            cargando = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Estadísticas generales
        if (!cargando && ejercicios.isNotEmpty()) {
            EstadisticasGenerales(ejercicios = ejercicios)
        }

        // Lista de ejercicios
        when {
            cargando -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF6C1AEF),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Cargando progreso...",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "❌",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            error!!,
                            fontSize = 16.sp,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            ejercicios.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "📚",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Aún no has completado ejercicios",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "¡Comienza a practicar para ver tu progreso aquí!",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ejercicios) { ejercicio ->
                        EjercicioCard(ejercicio = ejercicio)
                    }
                }
            }
        }
    }
}

@Composable
fun EstadisticasGenerales(ejercicios: List<Map<String, Any>>) {
    val totalEjercicios = ejercicios.size
    val ejerciciosPorMetodo = ejercicios.groupBy { 
        (it["id_ejercicio"] as? String)?.split("_")?.get(0) ?: "desconocido"
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "📊 Estadísticas Generales",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6C1AEF)
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                EstadisticaItem(
                    titulo = "Total",
                    valor = totalEjercicios.toString(),
                    icono = "🎯"
                )
                EstadisticaItem(
                    titulo = "Clásico",
                    valor = ejerciciosPorMetodo["clasico"]?.size?.toString() ?: "0",
                    icono = "📝"
                )
                EstadisticaItem(
                    titulo = "Ruso",
                    valor = ejerciciosPorMetodo["ruso"]?.size?.toString() ?: "0",
                    icono = "🇷🇺"
                )
                EstadisticaItem(
                    titulo = "Japonés",
                    valor = ejerciciosPorMetodo["japones"]?.size?.toString() ?: "0",
                    icono = "🇯🇵"
                )
                EstadisticaItem(
                    titulo = "Árabe",
                    valor = ejerciciosPorMetodo["arabe"]?.size?.toString() ?: "0",
                    icono = "🇸🇦"
                )
            }
        }
    }
}

@Composable
fun EstadisticaItem(
    titulo: String,
    valor: String,
    icono: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            icono,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            valor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6C1AEF)
        )
        Text(
            titulo,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun EjercicioCard(ejercicio: Map<String, Any>) {
                val idEjercicio = ejercicio["id_ejercicio"] as? String ?: "Sin ID"
                val puntaje = ejercicio["puntaje"] as? Long ?: 0
                val fecha = ejercicio["fecha"] as? Long ?: 0L
    val fechaFormateada = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(
                    Date(fecha)
                )

    // Parsear información del ejercicio
    val partes = idEjercicio.split("_")
    val metodo = partes.getOrNull(0) ?: "desconocido"
    val tabla = partes.getOrNull(1)?.replace("tabla", "") ?: "?"
    val ejercicioNum = partes.getOrNull(2) ?: "?"
    
    val (icono, color, nombreMetodo) = when (metodo) {
        "clasico" -> Triple("📝", Color(0xFF4CAF50), "Clásico")
        "ruso" -> Triple("🇷🇺", Color(0xFF2196F3), "Ruso")
        "japones" -> Triple("🇯🇵", Color(0xFFE91E63), "Japonés")
        "arabe" -> Triple("🇸🇦", Color(0xFFFF9800), "Árabe")
        else -> Triple("❓", Color.Gray, "Desconocido")
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del método
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    icono,
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Información del ejercicio
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "$nombreMetodo - Tabla $tabla",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    "Ejercicio #$ejercicioNum",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    fechaFormateada,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // Puntaje
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFF4CAF50),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    "✅ $puntaje",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}