package br.com.ebury

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FlightManagementApplication

fun main(args: Array<String>) {
    val dotenv =
        dotenv {
            ignoreIfMissing = false
        }

    dotenv.entries().forEach { entry ->
        System.setProperty(entry.key, entry.value)
    }

    runApplication<FlightManagementApplication>(*args)
}
