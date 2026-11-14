package com.example.lab0102

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { Surface { HelloComposeScreen() } } }
    }
}

/** Первое приветственное приложение на Compose. */
@Composable
fun HelloComposeScreen() {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFEAF2FF)).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Моё первое приложение", color = Color(0xFF174EA6), fontSize = 28.sp,
            fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(12.dp))
        Text("Автор приложения", color = Color(0xFF3B4658), fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        Text("Создано на Jetpack Compose", color = Color.Gray)
    }
}
