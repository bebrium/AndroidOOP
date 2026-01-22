import kotlin.random.Random
import kotlin.math.*

fun Double.format(decimals: Int): String = "%.${decimals}f".format(this)

open class Human(
    val fullName: String,
    val age: Int,
    override var currentSpeed: Double
) : Movable {
    override var x: Double = 0.0
    override var y: Double = 0.0

    override fun move(dt: Double) {
        val angle = Random.nextDouble() * 2 * PI
        x += currentSpeed * cos(angle) * dt
        y += currentSpeed * sin(angle) * dt
    }

    override fun printInfo() {
        println("$fullName | Возраст: $age | Скорость: ${currentSpeed.format(2)} м/с | " +
                "Координаты: (${x.format(2)}, ${y.format(2)})")
    }
}