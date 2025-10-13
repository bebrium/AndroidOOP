class Driver(
    fullName: String,
    age: Int,
    override var currentSpeed: Double,
    var direction: Double = Math.PI / 2
) : Human(fullName, age, currentSpeed) {

    override fun move(dt: Double) {
        val dx = currentSpeed * Math.cos(direction) * dt
        val dy = currentSpeed * Math.sin(direction) * dt
        x += dx
        y += dy
    }

    override fun printInfo() {
        println("$fullName | возраст: $age | скорость: ${"%.2f".format(currentSpeed)} м/с | координаты: (${x.format(2)}, ${y.format(2)}) | направление: ${direction.format(2)} рад")
    }

    private fun Double.format(digits: Int): String = "%.${digits}f".format(this)
}