package com.example.tablemath.ui.screens.exercises.models

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class Tablero {
    @Composable
    fun LadderScreen(
        metodo: String,
        onStepClick: (Int) -> Unit,
    ) {
        val steps = (1..10).map {Escalera(it)}
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ){
            Image(
                painter = painterResource(id = com.example.tablemath.R.drawable.serpiente),
                contentDescription = "Serpierte",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(255.dp)
            )
            Image(
                painter = painterResource(id = com.example.tablemath.R.drawable.escalera),
                contentDescription = "Escalera",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(260.dp)
                    .graphicsLayer(
                        rotationZ = -30f
                    )
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(9) { index ->
                        val step = Escalera(index + 1, completar = false)
                        LadderBox(step, metodo) {
                            onStepClick(step.numero)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                LadderBox(Escalera(10, completar = false), metodo) {
                    onStepClick(10)
                }
            }
        }
    }
    @Composable
    fun LadderBox(step: Escalera, metodo: String, onClick: () -> Unit) {
        val colors = listOf(
            Color(0xFFFFC107),
            Color(0xFF4CAF50),
            Color(0xFFE91E63),
            Color(0xFF9C27B0),
            Color(0xFF00BCD4),
            Color(0xFFFF5722),
            Color(0xFF8BC34A),
            Color(0xFF03A9F4),
            Color(0xFF795548),
            Color(0xFFFFD700)
        )
        val backgroundColor = if (step.numero == 10) colors.last() else colors[step.numero - 1]
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors[step.numero - 1])
                .clickable{ onClick() },
            contentAlignment = Alignment.Center

        ){
            Column(horizontalAlignment = Alignment.CenterHorizontally){
                Text(
                    text = "${step.numero}",
                    fontSize = 28.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                if(step.numero == 10)
                {
                    Text("\uD83C\uDFC6", fontSize = 26.sp)
                }else if(step.completar)
                {
                    Text("✔️", fontSize = 26.sp)
                }
                else
                {
                    Text("\uD83C\uDFB2", fontSize = 26.sp)
                }
            }
        }
    }
}