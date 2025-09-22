package com.example.tablemath.ui.screens.exercises

import android.os.Bundle
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tablemath.ui.screens.exercises.models.Tablero
import com.example.tablemath.ui.screens.exercises.ui.theme.TableMathTheme

class Ruso : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }
}

@Preview
@Composable
fun RusoPreview(){
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Método: Ruso",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Es un metodo divertido y facil, debes sacar dobles y mitades y luego sumar",
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 24.dp)
            )
            Tablero().LadderScreen("Clásico") { numero ->
                // Acción al hacer clic en una escalera
            }
        }
    }
}