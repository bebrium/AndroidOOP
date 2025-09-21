import kotlin.random.Random

fun main() {
    val humanCount = 3  // Можно 2-4, как в задании
    val simTime = 5     // Секунды симуляции

    // Создаём людей
    val humans = Array(humanCount) { i ->
        Human(
            fullName = "Негрик ${i + 1}",
            age = 18 + Random.nextInt(50),
            currentSpeed = 0.5 + Random.nextDouble() * 1.5
        )
    }

    // Создаём водителя
    val driver = Driver(
        fullName = "Элитный негрик водитель",
        age = 35,
        currentSpeed = 2.0,
        direction = Math.PI / 2 // Движение вверх (90 градусов)
    )

    // Симуляция по секундам
    for (sec in 1..simTime) {
        println("\n=== Секунда $sec ===")

        val threads = mutableListOf<Thread>()

        // Движение людей (в отдельных потоках)
        humans.forEach { human ->
            threads += Thread {
                human.move()
                human.printInfo()
            }
        }

        // Движение водителя (в отдельном потоке)
        threads += Thread {
            driver.move()
            driver.printInfo()
        }

        // Запуск всех потоков
        threads.forEach { it.start() }
        threads.forEach { it.join() } // Ждём завершения всех потоков перед следующей итерацией
    }

    println("\n✅ Симуляция завершена.")
}