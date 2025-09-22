package com.example.tablemath.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tablemath.data.model.Estudiante
import com.example.tablemath.domain.EstudianteRepository
import com.example.tablemath.ui.theme.TableMathTheme

class RegistroUsuario : ComponentActivity() {
    private lateinit var repository: EstudianteRepository  // solo declaras, no inicializas aquí

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        repository = EstudianteRepository() // ahora sí se inicializa correctamente
        setContent {
            TableMathTheme {
                RegistroUsuarioScreen { nombre, apellido, grado, pin ->
                    if (nombre.isBlank() || apellido.isBlank() || grado.isBlank() || pin.isBlank()) {
                        Toast.makeText(
                            this,
                            "Por favor, completa todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val estudiante = Estudiante(
                            nombre = nombre,
                            apellido = apellido,
                            grado = grado,
                            codigoEstudiante = pin
                        )
                        repository.registrarEstudiante(estudiante) { success, errorMessage ->
                            if (success) {
                                Toast.makeText(
                                    this,
                                    "Registro exitoso",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Navegar a la pantalla de login
                                val intent = Intent(this, Login::class.java)
                                startActivity(intent)
                                finish() // Opcional: para que el usuario no pueda volver a la pantalla de registro con el botón atrás
                            } else {
                                Toast.makeText(
                                    this,
                                    "Error en el registro: $errorMessage",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }
    @Composable
    fun RegistroUsuarioScreen(
        onRegisterClick: (String, String, String, String) -> Unit,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF66BB6A)),
            contentAlignment = Alignment.Center
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                //Texto de bienvenida
                Text(
                    text = "!Vamos, animate a registrarte en TableMath¡",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                //Variables para capturar los datos de registro
                var nombre by remember { mutableStateOf("") }
                var apellido by remember { mutableStateOf("") }
                var grado by remember { mutableStateOf("") }
                var pin by remember { mutableStateOf("") }

                //Formulario de registro
                OutlinedTextField( //Campo de texto para el nombre
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField( //Campo de texto para el apellido
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField( //Campo de texto para el grado
                    value = grado,
                    onValueChange = { grado = it },
                    label = { Text("Grado") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField( //Campo de texto para el codigo del estudiante
                    value = pin,
                    onValueChange = { pin = it },
                    label = { Text("Elige el pin que quieras") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Check, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                //Boton para registrarse
                Button(
                    onClick = { onRegisterClick(nombre, apellido, grado, pin) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Registrarse", fontSize = 18.sp, modifier = Modifier.padding(8.dp))
                }
                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }
        }
    }
}
