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
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tablemath.domain.EstudianteRepository
import com.example.tablemath.ui.theme.TableMathTheme

class Login : ComponentActivity() {
    private lateinit var repository: EstudianteRepository

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        repository = EstudianteRepository()
        setContent {
            TableMathTheme {
                Scaffold { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)){
                        LoginScreen{ codigoEstudiante ->
                            if(codigoEstudiante.isBlank())
                            {
                                Toast.makeText(this@Login, "Por favor ingresa el código del estudiante", Toast.LENGTH_SHORT).show()
                            }
                            else
                            {
                                repository.loginEstudiante(codigoEstudiante) { success, errorMessage, uid ->
                                    if(success && uid != null)
                                    {
                                        val intent = Intent(this@Login, Menu::class.java).apply {
                                            putExtra("ESTUDIANTE_ID", uid)
                                        }
                                        startActivity(intent)
                                        finish()
                                    }
                                    else
                                    {
                                        Toast.makeText(this@Login, "Error de inicio de sesión: $errorMessage", Toast.LENGTH_SHORT).show()
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
@Composable
fun LoginScreen(
    onLoginClick: (String) -> Unit
) {
    var codigoEstudiante by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF80DEEA)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Hola vamos a iniciar sesión",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = codigoEstudiante,
                onValueChange = { codigoEstudiante = it },
                label = { Text("Código del estudiante") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { onLoginClick(codigoEstudiante) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
