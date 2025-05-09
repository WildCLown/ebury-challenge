package br.com.ebury.config

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.ConfigurableEnvironment

@Configuration
class AppConfig : EnvironmentPostProcessor {
    override fun postProcessEnvironment(
        environment: ConfigurableEnvironment,
        application: SpringApplication,
    ) {
        val dotenv = Dotenv.load()
        dotenv.entries().forEach { entry ->
            environment.systemProperties[entry.key] = entry.value
        }
    }
}
