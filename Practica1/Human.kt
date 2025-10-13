import kotlin.random.Random

open class Human(
    val fullName: String,
    val age: Int,
    override var currentSpeed: Double
) : Movable {
    override var x: Double = 0.0
    override var y: Double = 0.0

    override fun move(dt: Double) {
        val angle = Random.nextDouble() * 2 * Math.PI
        val dx = currentSpeed * Math.cos(angle) * dt
        val dy = currentSpeed * Math.sin(angle) * dt
        x += dx
        y += dy
    }

    open fun printInfo() {
        println("$fullName | возраст: $age | скорость: ${"%.2f".format(currentSpeed)} м/c | координаты: (${x.format(2)}, ${y.format(2)})")
    }

    private fun Double.format(digits: Int) = "%.${digits}f".format(this)
}
    open fun printInfo() {
        println("$fullName | возраст: $age | скорость: ${"%.2f".format(currentSpeed)} м/c | координаты: (${x.format(2)}, ${y.format(2)})")
    }

    private fun Double.format(digits: Int) = "%.${digits}f".format(this)
}