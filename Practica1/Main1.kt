import kotlin.random.Random

fun main() {
    val humanCount = 3
    val simTime = 5

    val humans = Array(humanCount) { i ->
        Human(
            fullName = "Негрик ${i + 1}",
            age = 18 + Random.nextInt(50),
            currentSpeed = 0.5 + Random.nextDouble() * 1.5
        )
    }

    val driver = Driver(
        fullName = "Элитный негрик водитель",
        age = 35,
        currentSpeed = 2.0,
        direction = Math.PI / 2
    )

    for (sec in 1..simTime) {
        println("\n=== Секунда $sec ===")

        val threads = mutableListOf<Thread>()

        humans.forEach { human ->
            threads += Thread {
                human.move(1.0)
                human.printInfo()
            }
        }

        threads += Thread {
            driver.move(1.0)
            driver.printInfo()
        }

        threads.forEach { it.start() }
        threads.forEach { it.join() }
    }

    println("\n Симуляция завершена.")
}