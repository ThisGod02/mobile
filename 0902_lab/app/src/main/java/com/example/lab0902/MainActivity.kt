package com.example.lab0902

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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.room.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/** Локальная задача, сохранённая в Room. */
@Entity(tableName = "tasks")
data class Task(@PrimaryKey(autoGenerate = true) val id: Long = 0, val title: String, val description: String = "", val done: Boolean = false)
/** SQL-операции над задачами и поток изменений списка. */
@Dao
interface TaskDao {
    /** Emits the current task list and subsequent database updates. */
    @Query("SELECT * FROM tasks ORDER BY id DESC") fun observeAll(): Flow<List<Task>>
    /** Finds one task by its primary key. */
    @Query("SELECT * FROM tasks WHERE id = :id") suspend fun findById(id: Long): Task?
    /** Inserts a new task. */
    @Insert suspend fun insert(task: Task)
    /** Persists changed task fields. */
    @Update suspend fun update(task: Task)
    /** Removes a task from the database. */
    @Delete suspend fun delete(task: Task)
}
/** Room-база итогового приложения. */
@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    /** Returns the DAO used by the repository layer. */
    abstract fun taskDao(): TaskDao
    companion object { @Volatile private var instance: TaskDatabase? = null
        /** Returns the process-wide Room database instance. */
        fun get(context: android.content.Context): TaskDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, TaskDatabase::class.java, "taskflow.db").build().also { instance = it }
        }
    }
}

/** MVVM ViewModel exposes task data as a read-only StateFlow. */
class TasksViewModel(private val dao: TaskDao) : ViewModel() {
    val tasks: StateFlow<List<Task>> = dao.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    /** Создаёт задачу в локальной Room-базе. */
    fun add(title: String) { if (title.isNotBlank()) viewModelScope.launch { dao.insert(Task(title = title.trim())) } }
    /** Переключает состояние выполнения задачи. */
    fun toggle(task: Task) { viewModelScope.launch { dao.update(task.copy(done = !task.done)) } }
    /** Удаляет задачу из хранилища. */
    fun remove(task: Task) { viewModelScope.launch { dao.delete(task) } }
}

/** Android-точка входа приложения TaskFlow. */
class MainActivity : ComponentActivity() {
    /** Configures Room and renders the Compose application. */
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); val dao = TaskDatabase.get(this).taskDao(); setContent { MaterialTheme { TaskFlowApp(dao) } } }
}

/** Связывает экран списка и экран задачи через Navigation Compose. */
@Composable
fun TaskFlowApp(dao: TaskDao) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "tasks") {
        composable("tasks") { TaskListScreen(dao, onOpen = { nav.navigate("detail/${it.id}") }) }
        composable("detail/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) { entry ->
            val taskId = entry.arguments?.getLong("id") ?: 0L
            TaskDetailScreen(dao, taskId, onBack = { nav.popBackStack() })
        }
    }
}

@Composable
private fun TaskListScreen(dao: TaskDao, onOpen: (Task) -> Unit) {
    val factory = remember(dao) { object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return TasksViewModel(dao) as T
        }
    } }
    val vm: TasksViewModel = viewModel(factory = factory)
    val tasks by vm.tasks.collectAsStateWithLifecycle(); var title by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("TaskFlow", style = MaterialTheme.typography.headlineLarge)
        Text("Задачи хранятся локально в Room", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(title, { title = it }, label = { Text("Новая задача") }, modifier = Modifier.weight(1f), singleLine = true)
            Button(onClick = { vm.add(title); title = "" }, modifier = Modifier.padding(top = 8.dp)) { Text("Добавить") }
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn { items(tasks, key = { it.id }) { task ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), onClick = { onOpen(task) }) {
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Checkbox(checked = task.done, onCheckedChange = { vm.toggle(task) })
                    Text(task.title, Modifier.weight(1f).padding(top = 12.dp), style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { vm.remove(task) }) { Text("Удалить") }
                }
            }
        } }
    }
}

/** Загружает и показывает сведения о выбранной задаче. */
@Composable
private fun TaskDetailScreen(dao: TaskDao, taskId: Long, onBack: () -> Unit) {
    var task by remember(taskId) { mutableStateOf<Task?>(null) }
    LaunchedEffect(taskId) { task = dao.findById(taskId) }
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        TextButton(onClick = onBack) { Text("← К списку") }
        Text("Детали задачи", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text(task?.title ?: "Загрузка…", style = MaterialTheme.typography.titleLarge)
        Text(task?.description?.ifBlank { "Описание не задано." } ?: "")
        Text("Статус: ${if (task?.done == true) "выполнена" else "в процессе"}")
    }
}
