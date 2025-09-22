package com.example.tablemath.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.tablemath.ui.theme.TableMathTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tablemath.R
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TableMathTheme {
                SplashScreen {
                    startActivity(Intent(this, Bienvenida::class.java))
                    finish()
                }
            }
        }
    }
}
@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var showContent by remember { mutableStateOf(false) }
    var animate by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (animate) 1.2f else 1f,
        animationSpec = tween(
            durationMillis = 800
        )
    )
    LaunchedEffect(Unit) {
        delay(1000)
        showContent = true
        animate =   true
        delay(2000)
        onTimeout()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D47A1)),
        contentAlignment = Alignment.Center
    ){
        if(showContent)
        {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ){
                Image(
                    painter = painterResource(id = R.drawable.numby),
                    contentDescription = "Mascota",
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scale)
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Text(
                    text = "TableMath",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.scale(scale)
                )
            }
        }
    }
}