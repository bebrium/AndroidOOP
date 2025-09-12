import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Human(
    var fullName: String,
    var age: Int,
    var currentSpeed: Double
) {
    private var x = 0.0
    private var y = 0.0

    fun move(dt: Double = 1.0) {
        val angle = Random.nextDouble() * 2 * Math.PI
        x += currentSpeed * cos(angle) * dt
        y += currentSpeed * sin(angle) * dt
    }

    fun printInfo() {
        println("$fullName | возраст: $age | скорость: ${"%.2f".format(currentSpeed)} м/с | координаты: (${x.format(2)}, ${y.format(2)})")
    }

    private fun Double.format(digits: Int) = "%.${digits}f".format(this)
}

fun main() {
    val humanCount = 24
    val simTime = 5    // ← длительность симуляции в секундах

    // Создаём массив людей
    val humans = Array(humanCount) { i ->
        Human(
            fullName = "Негрик ${i + 1}",
            age = 18 + Random.nextInt(50),
            currentSpeed = 0.5 + Random.nextDouble() * 1.5
        )
    }

    // Симуляция по секундам
    for (sec in 1..simTime) {
        println("\n === Секунда $sec ===")
        humans.forEach { human ->
            human.move()
            human.printInfo()
        }
    }

    println("\nСимуляция завершена.")
}