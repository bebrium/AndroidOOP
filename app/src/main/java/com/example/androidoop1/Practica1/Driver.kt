class Driver(
    fullName: String,
    age: Int,
    currentSpeed: Double
) : Human(fullName, age, currentSpeed) {


    override fun move(dt: Double) {
        x += currentSpeed * dt
    }

    override fun printInfo() {
        println("$fullName (Водитель) | Возраст: $age | Скорость: ${currentSpeed.format(2)} м/с | " +
                "Координаты: (${x.format(2)}, ${y.format(2)})")
    }
}