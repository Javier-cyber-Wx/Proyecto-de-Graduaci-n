package com.example.tablemath.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.example.tablemath.data.model.Estudiante
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Perfil : ComponentActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TableMathTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF5F5F5) // fondo gris claro
                ) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        PerfilScreen(
                            uid = uid,
                            onLogout = {
                                startActivity(Intent(this, Login::class.java))
                            }
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No hay usuario autenticado",
                                color = Color.Red,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun PerfilScreen(
    uid: String,
    onLogout: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var estudiante by remember { mutableStateOf<Estudiante?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uid) {
        db.collection("estudiantes")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    estudiante = document.toObject(Estudiante::class.java)
                } else {
                    error = "No se encontró el estudiante"
                }
                cargando = false
            }
            .addOnFailureListener { e ->
                error = "Error: ${e.message}"
                cargando = false
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            cargando -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF6C1AEF)
                )
            }
            error != null -> {
                Text(
                    text = error ?: "Error desconocido",
                    color = Color.Red,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            estudiante != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // Card con información
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${estudiante!!.nombre} ${estudiante!!.apellido}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6C1AEF)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Grado: ${estudiante!!.grado}",
                                fontSize = 18.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Código: ${estudiante!!.codigoEstudiante}",
                                fontSize = 18.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botón cerrar sesión
                    Button(
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C1AEF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Cerrar sesión", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}