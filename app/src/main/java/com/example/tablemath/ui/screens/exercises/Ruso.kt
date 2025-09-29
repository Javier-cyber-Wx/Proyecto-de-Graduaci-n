package com.example.tablemath.ui.screens.exercises

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tablemath.ui.screens.exercises.models.Ejercicios
import com.example.tablemath.ui.screens.exercises.models.Tablero
import com.example.tablemath.ui.screens.exercises.ui.theme.TableMathTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
class Ruso : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TableMathTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White // Fondo blanco
                ) {
                    var tablaSeleccionada by remember { mutableStateOf<Int?>(null) }
                    val userState = remember { mutableStateOf(Firebase.auth.currentUser) }

                    LaunchedEffect(Unit) {
                        Firebase.auth.addAuthStateListener { firebaseAuth ->
                            userState.value = firebaseAuth.currentUser
                        }
                    }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(), // Asegura que Scaffold use todo el espacio
                        containerColor = Color.Transparent, // Scaffold no pinta fondo
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (tablaSeleccionada == null) {
                                Text(
                                    text = "Método: Ruso",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Escoge la tabla que deseas practicar:",
                                    fontSize = 20.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Tablero().LadderScreen("Ruso") { numero ->
                                    tablaSeleccionada = numero
                                }
                            } else {
                                val usuario = userState.value
                                if (usuario != null) {
                                    Ejercicios().ExerciseScreenRuso(
                                        tabla = tablaSeleccionada!!,
                                        estudianteId = usuario.uid,
                                        onFinish = { tablaSeleccionada = null }
                                    )
                                } else {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator()
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text("Cargando usuario…")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


