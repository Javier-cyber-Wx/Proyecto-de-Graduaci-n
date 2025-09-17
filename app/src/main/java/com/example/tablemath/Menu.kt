package com.example.tablemath
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tablemath.Login
import com.example.tablemath.R
import com.example.tablemath.RegistroUsuario
import com.example.tablemath.ui.theme.TableMathTheme
class Menu : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TableMathTheme {
                // 📌 Aquí llamamos al menú principal
                MainMenuScreen(
                    onMetodoClick = { metodo ->
                        when (metodo) {
                            "Tradicional" -> {
                                startActivity(Intent(this, Login::class.java))
                            }
                            "Ruso" -> {
                                startActivity(Intent(this, RegistroUsuario::class.java))
                            }
                            "Japones" -> {
                                startActivity(Intent(this, Login::class.java))
                            }
                            "Arabe" -> {
                                startActivity(Intent(this, RegistroUsuario::class.java))
                            }
                        }
                    },
                    onProgresoClick = {
                        // Navegar a la pantalla de progreso
                        // startActivity(Intent(this, ProgresoActivity::class.java))
                    },
                    onPerfilClick = {
                        // Navegar a la pantalla de perfil
                        // startActivity(Intent(this, PerfilActivity::class.java))
                    }
                )
            }
        }
    }
}


@Composable
fun MainMenuScreen(
    onMetodoClick: (String) -> Unit,
    onProgresoClick: () -> Unit,
    onPerfilClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF42A5F5)), // Azul alegre como Duolingo
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título de la App
            Text(
                text = "¡Elige tu camino con Numby!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Opciones del Menú en estilo circular (como Duolingo)
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                MenuOption(
                    text = "Tradicional",
                    color = Color(0xFFFFC107),
                    iconRes = R.drawable.ic_tradicional,
                    onClick = { onMetodoClick("Tradicional") }
                )
                MenuOption(
                    text = "Ruso",
                    color = Color(0xFF4CAF50),
                    iconRes = R.drawable.ic_ruso,
                    onClick = { onMetodoClick("Ruso") }
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                MenuOption(
                    text = "Japonés",
                    color = Color(0xFFE91E63),
                    iconRes = R.drawable.ic_japones,
                    onClick = { onMetodoClick("Japones") }
                )
                MenuOption(
                    text = "Árabe",
                    color = Color(0xFF9C27B0),
                    iconRes = R.drawable.ic_arabe,
                    onClick = { onMetodoClick("Arabe") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botones secundarios
            Button(
                onClick = onProgresoClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text("Ver mi Progreso", fontSize = 18.sp, color = Color.White)
            }

            Button(
                onClick = onPerfilClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688)),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text("Mi Perfil", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}
@Composable
fun MenuOption(text: String, color: Color, iconRes: Int, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.size(50.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text, fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}
@Preview(showBackground = true, showSystemUi = true, name = "Preview Menu Activity")
@Composable
fun MenuActivityPreview() {
    TableMathTheme {
        // Simulamos la Activity completa
        MainMenuScreen(
            onMetodoClick = { metodo ->
                // Para preview no necesitamos acción real
            },
            onProgresoClick = {
                // Para preview no necesitamos acción real
            },
            onPerfilClick = {
                // Para preview no necesitamos acción real
            }
        )
    }
}

