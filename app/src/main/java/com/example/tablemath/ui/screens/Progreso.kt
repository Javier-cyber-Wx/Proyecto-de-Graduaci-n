package com.example.tablemath.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { paddingValues ->
                        Column(modifier = Modifier.padding(paddingValues)) {
                            val estudianteId = intent.getStringExtra("estudianteId") ?: ""
                            ProgresoScreen(estudianteId)
                        }
                    }
                )
                }
            }
        }
    }
@Composable
fun ProgresoScreen(estudianteId: String) {
    val db = FirebaseFirestore.getInstance()
    var ejercicios by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    LaunchedEffect(estudianteId) {
        db.collection("progreso")
            .whereEqualTo("id_alumno", estudianteId)
            .get()
            .addOnSuccessListener { result ->
                ejercicios = result.documents.mapNotNull { it.data }
            }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("📈 Mi Progreso", style = MaterialTheme.typography.headlineMedium)

        if (ejercicios.isEmpty()) {
            Text("Aún no has completado ejercicios.")
        } else {
            ejercicios.forEach { ejercicio ->
                val idEjercicio = ejercicio["id_ejercicio"] as? String ?: "Sin ID"
                val puntaje = ejercicio["puntaje"] as? Long ?: 0
                val fecha = ejercicio["fecha"] as? Long ?: 0L
                val fechaFormateada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                    Date(fecha)
                )

                Text("🧮 $idEjercicio - Puntaje: $puntaje - Fecha: $fechaFormateada")
            }
        }
    }
}