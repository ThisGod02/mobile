package com.example.lab0502

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Неизменяемое состояние счётчика для передачи в UI. */
data class CounterState(val count: Int = 0, val step: Int = 1, val loading: Boolean = true)

/** Хранит и изменяет состояние счётчика, не смешивая бизнес-логику с UI. */
class CounterViewModel : ViewModel() {
    private val _state = MutableStateFlow(CounterState())
    val state: StateFlow<CounterState> = _state.asStateFlow()
    init { loadInitialValue() }
    /** Асинхронно подготавливает начальное значение. */
    fun loadInitialValue() { viewModelScope.launch { delay(350); _state.update { it.copy(loading = false) } } }
    /** Увеличивает число на выбранный шаг. */
    fun increment() = _state.update { it.copy(count = it.count + it.step) }
    /** Уменьшает число на выбранный шаг. */
    fun decrement() = _state.update { it.copy(count = it.count - it.step) }
    /** Сбрасывает число. */
    fun reset() = _state.update { it.copy(count = 0) }
    /** Обновляет шаг счётчика. */
    fun setStep(step: Int) = _state.update { it.copy(step = step) }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { MaterialTheme { CounterScreen() } } }
}

/** UI подписывается на StateFlow и отправляет команды ViewModel. */
@Composable
fun CounterScreen(vm: CounterViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("MVVM Counter", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        if (state.loading) CircularProgressIndicator() else Text("${state.count}", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { OutlinedButton(onClick = vm::decrement) { Text("−") }; Button(onClick = vm::increment) { Text("+") } }
        Spacer(Modifier.height(12.dp)); Text("Шаг: ${state.step}")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf(1, 2, 5).forEach { OutlinedButton(onClick = { vm.setStep(it) }) { Text("$it") } } }
        TextButton(onClick = vm::reset) { Text("Сброс") }
    }
}
