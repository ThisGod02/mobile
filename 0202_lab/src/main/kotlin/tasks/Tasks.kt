package tasks

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay

data class Person(val name: String, val age: Int)

/** Решения задач по базовому синтаксису Kotlin. */
object BasicSyntax {
    fun greetUser(name: String, age: Int): String = "Привет, $name! Через 10 лет тебе будет ${age + 10} лет."
    fun getSeason(month: Int): String = when (month) {
        12, 1, 2 -> "Зима"; 3, 4, 5 -> "Весна"; 6, 7, 8 -> "Лето"; 9, 10, 11 -> "Осень"; else -> "Некорректный месяц"
    }
    fun factorial(n: Int): Long {
        if (n < 0) return -1
        tailrec fun loop(current: Int, accumulator: Long): Long = if (current <= 1) accumulator else loop(current - 1, accumulator * current)
        return loop(n, 1)
    }
}

/** Решения задач по null-безопасности. */
object NullSafety {
    fun parseIntSafe(str: String?): Int? = str?.toIntOrNull()
    fun filterNonNullAndDouble(list: List<Int?>): List<Int> = list.filterNotNull().map { it * 2 }
}

/** Решения задач по коллекциям и функциям высшего порядка. */
object Collections {
    fun averageAge(people: List<Person>): Double = if (people.isEmpty()) 0.0 else people.map { it.age }.average()
    fun groupByFirstLetter(words: List<String>): Map<Char, List<String>> = words.filter { it.isNotBlank() }.groupBy { it.first().uppercaseChar() }
    fun findLongestWord(words: List<String>): String? = words.maxByOrNull { it.length }
}

/** Решения задач по корутинам. */
object Coroutines {
    suspend fun delayedPrint(message: String, delayMs: Long) { println(message); delay(delayMs); println("Готово!") }
    suspend fun runParallelTasks(): List<String> = coroutineScope {
        val completed = Channel<String>(capacity = 3)
        val jobs = listOf(1000L to "Результат 1", 2000L to "Результат 2", 3000L to "Результат 3").map { (ms, result) ->
            async { delay(ms); completed.send(result) }
        }
        val results = List(3) { completed.receive() }
        jobs.awaitAll()
        completed.close()
        results
    }

    /** Анализирует список чисел или текст из строки (дополнительная задача). */
    fun processUserInput(input: String?): Map<String, Any> {
        val value = input?.trim().orEmpty()
        if (value.isEmpty()) return mapOf("error" to "Пустой ввод")
        val parts = value.split(',').map { it.trim() }
        val numbers = parts.mapNotNull { it.toIntOrNull() }
        if (numbers.size == parts.size) return mapOf(
            "sum" to numbers.sum(), "average" to numbers.average(), "count" to numbers.size, "sorted" to numbers.sorted()
        )
        val words = value.split(Regex("\\s+")).filter { it.isNotBlank() }
        return mapOf("wordCount" to words.size, "longestWord" to (words.maxByOrNull { it.length } ?: ""), "charCount" to value.count { !it.isWhitespace() })
    }
}
