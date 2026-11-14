package com.example.lab0602

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
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "notes")
data class Note(@PrimaryKey(autoGenerate = true) val id: Long = 0, val title: String, val body: String)

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY id DESC") fun observeAll(): Flow<List<Note>>
    @Insert suspend fun insert(note: Note)
    @Delete suspend fun delete(note: Note)
}

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    companion object { @Volatile private var INSTANCE: NotesDatabase? = null
        fun get(context: android.content.Context): NotesDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context.applicationContext, NotesDatabase::class.java, "notes.db").build().also { INSTANCE = it }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); val dao = NotesDatabase.get(this).noteDao(); setContent { MaterialTheme { NotesScreen(dao) } } }
}

@Composable
fun NotesScreen(dao: NoteDao) {
    val notes by dao.observeAll().collectAsState(initial = emptyList())
    var title by remember { mutableStateOf("") }; var body by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Заметки · Room", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(title, { title = it }, label = { Text("Заголовок") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(body, { body = it }, label = { Text("Текст") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = { if (title.isNotBlank()) { scope.launch { dao.insert(Note(title = title.trim(), body = body.trim())); title = ""; body = "" } } }, modifier = Modifier.padding(vertical = 8.dp)) { Text("Добавить заметку") }
        HorizontalDivider()
        LazyColumn { items(notes, key = { it.id }) { note -> Card(Modifier.fillMaxWidth().padding(vertical = 5.dp)) { Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) { Text(note.title, style = MaterialTheme.typography.titleMedium); Text(note.body) }
            TextButton(onClick = { scope.launch { dao.delete(note) } }) { Text("Удалить") }
        } } } }
    }
}
