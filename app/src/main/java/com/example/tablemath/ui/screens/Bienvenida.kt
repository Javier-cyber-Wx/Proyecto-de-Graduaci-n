package com.example.tablemath.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tablemath.R
import com.example.tablemath.ui.theme.TableMathTheme
import kotlinx.coroutines.delay


class Bienvenida : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TableMathTheme {
                WelcomeScreen(
                    onRegisterClick = {
                        startActivity(Intent(this, RegistroUsuario::class.java))
                    },
                    onLoginClick = {
                        startActivity(Intent(this, Login::class.java))
                    }
                )
            }
        }
    }
}
@Composable
fun NumbyAnimado()
{
    val saludoFrame = listOf(
        R.drawable.numby_animation_1, R.drawable.numby_animation_2, R.drawable.numby_animation_3
    )
    val hablaFrame = listOf(
        R.drawable.animboca_1, R.drawable.animboca_2, R.drawable.animboca_3, R.drawable.animboca_4
    )
    var currentSaludoFrame by remember { mutableStateOf(0) }
    var enSaludo by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        repeat(6)
        {
            currentSaludoFrame = (currentSaludoFrame + 1) % saludoFrame.size
            delay(400)
        }
        enSaludo = false
    }
    LaunchedEffect(enSaludo) {
        if (!enSaludo) {
            while (true) {
                currentSaludoFrame = (currentSaludoFrame + 1) % hablaFrame.size
                delay(250)
            }
        }
    }
    Image(
        painter = painterResource(
            id = if(enSaludo) saludoFrame[currentSaludoFrame] else hablaFrame[currentSaludoFrame]
        ),
        contentDescription = "Numby Animado",
        modifier = Modifier.size(220.dp)
    )
}
@Composable
fun SpeechBubble(text: String, bgColor: Color = Color.White, borderColor: Color = Color(0xFF42A5F5)) {
    Box(
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .background(Color(0xFF90CAF9), RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .widthIn(min = 200.dp, max = 300.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFF1E293B),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
@Composable
fun MotivationalSpeechBubble() {
    val mensajes = listOf(
        "!Vamos a multiplicar con diversión!",
        "Si, ya tienes una cuenta, inicia sesión",
        "Si no tiene no te preocupes, crea una cuenta, yo te ayudo"
    )
    var currentMessage by remember { mutableStateOf(mensajes[0]) }
    LaunchedEffect(Unit)
    {
        while (true){
            for (mensaje in mensajes){
                currentMessage = mensaje
                delay(3000)
            }
        }
    }
    SpeechBubble(
        text = currentMessage,
        bgColor = Color(0xFFFFF59D),
        borderColor = Color(0xFFFBC02D)
    )
}
@Composable
fun NumbyBurbuja(messages: List<String>){
    var currentMessage by remember {mutableStateOf(messages[0])}
    LaunchedEffect(Unit)
    {
        while (true){
            for (mensaje in messages){
                currentMessage = mensaje
                delay(2500)
            }
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Globo de texto ARRIBA
        SpeechBubble(
            text = currentMessage,
            bgColor = Color(0xFFFFF59D),
            borderColor = Color(0xFFFBC02D)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Numby Animado
        NumbyAnimado()
    }
}
@Composable
fun WelcomeScreen(onRegisterClick: () -> Unit, onLoginClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF42A5F5), Color(0xFF1976D2))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Numby con globo motivacional
            NumbyBurbuja(
                messages = listOf(
                    "¡Vamos a aprender!",
                    "¡Inicia sesión rápido!",
                    "¡Yo te ayudo a registrarte!"
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Texto de bienvenida
            Text(
                text = "Bienvenido a TableMath",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Botón de Crear cuenta
            Button(
                onClick = onRegisterClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text(
                    text = "Crear Cuenta",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón de Iniciar sesión
            Button(
                onClick = onLoginClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
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
@Composable
@Preview
fun WelcomeScreenPreview() {
    TableMathTheme {
        WelcomeScreen(onRegisterClick = {}, onLoginClick = {})
    }
}