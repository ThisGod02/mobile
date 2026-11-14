package com.example.lab0702

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class TodoDto(val id: Int, val title: String, val completed: Boolean)
interface TodoApi { @GET("todos") suspend fun getTodos(@Query("_limit") limit: Int = 20): List<TodoDto> }
object ApiClient { val api: TodoApi by lazy { Retrofit.Builder().baseUrl("https://jsonplaceholder.typicode.com/").addConverterFactory(GsonConverterFactory.create()).build().create(TodoApi::class.java) } }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { MaterialTheme { CatalogScreen() } } }
}

@Composable
fun CatalogScreen() {
    var items by remember { mutableStateOf(emptyList<TodoDto>()) }; var error by remember { mutableStateOf<String?>(null) }; var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Каталог задач · Retrofit", style = MaterialTheme.typography.headlineSmall)
        Button(onClick = { scope.launch { loading = true; error = null; try { items = ApiClient.api.getTodos() } catch (e: Exception) { error = e.localizedMessage ?: "Не удалось загрузить данные" }; loading = false } }, modifier = Modifier.padding(vertical = 12.dp)) { Text("Загрузить из API") }
        if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        error?.let { Text("Ошибка: $it", color = MaterialTheme.colorScheme.error) }
        LazyColumn { items(items, key = { it.id }) { todo -> ListItem(headlineContent = { Text(todo.title) }, supportingContent = { Text(if (todo.completed) "Выполнено" else "В процессе") }) } }
    }
}
