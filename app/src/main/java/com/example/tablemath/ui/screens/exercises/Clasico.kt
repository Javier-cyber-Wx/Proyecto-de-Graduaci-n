package com.example.tablemath.ui.screens.exercises

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tablemath.ui.screens.exercises.models.Ejercicios
import com.example.tablemath.ui.screens.exercises.models.Escalera
import com.example.tablemath.ui.screens.exercises.models.Tablero
import com.example.tablemath.ui.screens.exercises.ui.theme.TableMathTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class Clasico : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Obtener dinámicamente el UID del usuario logueado
        val estudianteId = Firebase.auth.currentUser?.uid

        setContent {
            TableMathTheme {
                var tablaSeleccionada by remember { mutableStateOf<Int?>(null) }

                Scaffold { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        if (tablaSeleccionada == null) {
                            Text(
                                text = "Método: Clásico",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Escoge la tabla que deseas practicar:",
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 24.dp)
                            )
                            Tablero().LadderScreen("Clásico") { numero ->
                                tablaSeleccionada = numero
                            }
                        } else {
                            estudianteId?.let { id ->
                                Ejercicios().ExerciseScreen(
                                    metodo = "clasico",
                                    tabla = tablaSeleccionada!!,
                                    estudianteId = id,
                                    onFinish = { tablaSeleccionada = null }
                                )
                            } ?: run {
                                Text("Error: Usuario no autenticado")
                            }
                        }
                    }
                }
            }
        }
    }
}
