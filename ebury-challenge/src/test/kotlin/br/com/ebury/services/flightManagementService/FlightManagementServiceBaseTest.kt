package br.com.ebury.services.flightManagementService

import br.com.ebury.repository.FlightRouteRepository
import br.com.ebury.service.impl.FlightManagementServiceImpl
import io.mockk.clearMocks
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach

open class FlightManagementServiceBaseTest {
    val flightRouteRepositoryMock= mockk<FlightRouteRepository>()

    val impl = FlightManagementServiceImpl(
        flightRouteRepository = flightRouteRepositoryMock,
    )

    @AfterEach
    fun tearDown() {
        clearMocks(
            flightRouteRepositoryMock,
        )
    }
}