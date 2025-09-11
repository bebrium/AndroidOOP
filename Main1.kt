import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Класс Human
class Human(
    var fullName: String,
    var age: Int,
    var currentSpeed: Double
) {
    private var x = 0.0
    private var y = 0.0

    fun getPosition() = "($x, $y)"

    // Random Walk
    fun move(dt: Double = 1.0) {
        val angle = Random.nextDouble() * 2 * Math.PI
        x += currentSpeed * cos(angle) * dt
        y += currentSpeed * sin(angle) * dt
        println("$fullName → (${x.format(2)}, ${y.format(2)})")
    }

    private fun Double.format(digits: Int) = "%.${digits}f".format(this)
}

fun main() {
    val humanCount = 24 // ← замени на свой номер в списке!
    val simTime = 5    // ← время симуляции

    val humans = Array(humanCount) { i ->
        Human(
            "Негрики ${i + 1}",
            18 + Random.nextInt(50),
            0.5 + Random.nextDouble() * 1.5
        )
    }

    for (sec in 1..simTime) {
        println("\n⏱️  Секунда $sec:")
        humans.forEach { it.move() }
    }

    println("\n🏁 Готово.")
}