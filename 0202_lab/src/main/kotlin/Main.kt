import kotlinx.coroutines.runBlocking
import tasks.*

fun main() {
    println("=== Лабораторная работа №2 ===")
    println(BasicSyntax.greetUser("Анна", 25))
    println(BasicSyntax.getSeason(3))
    println("Факториал 5 = ${BasicSyntax.factorial(5)}")
    println(NullSafety.parseIntSafe("123"))
    println(NullSafety.filterNonNullAndDouble(listOf(1, null, 3, null, 5)))
    val people = listOf(Person("Анна", 25), Person("Иван", 30), Person("Мария", 35))
    println("Средний возраст = ${Collections.averageAge(people)}")
    println(Collections.groupByFirstLetter(listOf("apple", "apricot", "banana")))
    println(Collections.findLongestWord(listOf("apple", "banana", "pineapple")))
    runBlocking { Coroutines.delayedPrint("Начинаем...", 250); println(Coroutines.runParallelTasks()) }
}
