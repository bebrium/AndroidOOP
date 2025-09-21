class Driver(
    fullName: String,
    age: Int,
    currentSpeed: Double,
    var direction: Double = Math.PI / 4 // Направление по умолчанию — 45 градусов
) : Human(fullName, age, currentSpeed) {

    override fun move(dt: Double) {
        val dx = currentSpeed * Math.cos(direction) * dt
        val dy = currentSpeed * Math.sin(direction) * dt
        x += dx
        y += dy
    }

    override fun printInfo() {
        println("$fullName | возраст: $age | скорость: ${"%.2f".format(currentSpeed)} м/c | координаты: (${x.format(2)}, ${y.format(2)}) | направление: ${"%.2f".format(direction)} рад")
    }

    private fun Double.format(digits: Int) = "%.${digits}f".format(this)
}