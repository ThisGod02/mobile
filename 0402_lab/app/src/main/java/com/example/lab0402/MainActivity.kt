package com.example.lab0402

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { MaterialTheme { NavigationDemo() } } }
}

/** Демонстрация навигационного back stack и системной кнопки «Назад». */
@Composable
fun NavigationDemo() {
    val nav = rememberNavController()
    CompositionLocalProvider(LocalNavController provides nav) { NavHost(navController = nav, startDestination = "home") {
        composable("home") { Page(title = "Главный экран", body = "Добро пожаловать!", action = "Открыть профиль") { nav.navigate("profile/Студент") } }
        composable("profile/{name}", arguments = listOf(navArgument("name") { type = NavType.StringType })) { entry ->
            val name = entry.arguments?.getString("name").orEmpty()
            Page(title = "Профиль", body = "Пользователь: $name", action = "К деталям") { nav.navigate("details") }
        }
        composable("details") { Page(title = "Детали", body = "Экран демонстрирует отдельный маршрут.", action = "На главную") { nav.popBackStack("home", inclusive = false) } }
    } }
}

@Composable
private fun Page(title: String, body: String, action: String, onAction: () -> Unit) {
    val nav = LocalNavController.current
    BackHandler(enabled = nav.previousBackStackEntry != null) { nav.popBackStack() }
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp)); Text(body)
        Spacer(Modifier.height(24.dp)); Button(onClick = onAction) { Text(action) }
        TextButton(onClick = { nav.popBackStack() }) { Text("Назад") }
    }
}

private val LocalNavController = compositionLocalOf<androidx.navigation.NavHostController> { error("NavController missing") }
