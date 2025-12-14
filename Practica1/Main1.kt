import kotlin.concurrent.thread

fun main() {
    val humans = listOf(
        Human("Иванов Иван", 25, 1.2),
        Human("Петрова Анна", 30, 1.0),
        Driver("Сидоров Алексей", 40, 2.0)
    )

    val simulationTime = 10
    val dt = 0.1

    val threads = humans.map { human ->
        thread {
            repeat((simulationTime / dt).toInt()) {
                human.move(dt)
                Thread.sleep((dt * 1000).toLong())
            }
        }
    }

    threads.forEach { it.join() }


    humans.forEach { it.printInfo() }
}
