package com.example.lab0302

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { MaterialTheme { ProfileScreen() } } }
}

/** Демонстрационный интерактивный экран профиля. */
@Composable
fun ProfileScreen() {
    val initialName = "Алексей Иванов"
    var name by remember { mutableStateOf(initialName) }
    var draft by remember { mutableStateOf(initialName) }
    var subscribed by remember { mutableStateOf(false) }
    var followers by remember { mutableIntStateOf(128) }
    val accent by animateColorAsState(if (subscribed) Color(0xFF27866A) else Color(0xFF4659C9), label = "accent")
    Column(Modifier.fillMaxSize().background(Color(0xFFF6F7FB)).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(30.dp))
        Box(Modifier.size(104.dp).clip(CircleShape).background(Color(0xFFE1E6FF)), contentAlignment = Alignment.Center) {
            Text("АИ", color = accent, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(18.dp))
        Text(name, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Разработчик · Онлайн", color = Color.Gray)
        Spacer(Modifier.height(18.dp))
        Text("$followers подписчиков", color = accent, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(16.dp))
        Button(onClick = { subscribed = !subscribed; followers += if (subscribed) 1 else -1 }, colors = ButtonDefaults.buttonColors(containerColor = accent)) {
            Text(if (subscribed) "Отписаться" else "Подписаться")
        }
        HorizontalDivider(Modifier.padding(vertical = 20.dp))
        OutlinedTextField(value = draft, onValueChange = { draft = it }, label = { Text("Имя профиля") }, singleLine = true)
        Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { name = draft.trim().ifBlank { initialName } }) { Text("Сохранить") }
            OutlinedButton(onClick = { draft = initialName; name = initialName }) { Text("Сброс") }
        }
    }
}
