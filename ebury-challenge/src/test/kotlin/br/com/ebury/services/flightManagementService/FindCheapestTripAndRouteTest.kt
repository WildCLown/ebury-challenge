package br.com.ebury.services.flightManagementService

import br.com.ebury.fixtures.FlightRouteFixture
import br.com.ebury.model.ShortestRouteAndPrice
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FindCheapestTripAndRouteTest : FlightManagementServiceBaseTest() {

    @Test
    fun `should return the cheapest trip and route`() {
        every { flightRouteRepositoryMock.findAll() } returns FlightRouteFixture.listFlights

        val result = impl.findCheapestTripAndRoute(
            takeOffAirportCode = "GRU",
            landingAirportCode = "CDG"
        )

        val expected = ShortestRouteAndPrice(
            totalPrice = 40,
            airportRoute = listOf("GRU", "BRC", "SCL", "ORL", "CDG")
        )
        assertEquals(expected, result)
        verify { flightRouteRepositoryMock.findAll() }
    }

    @Test
    fun `should return empty route when no path exists`() {
        every { flightRouteRepositoryMock.findAll() } returns FlightRouteFixture.listFlights

        val result = impl.findCheapestTripAndRoute(
            takeOffAirportCode = "GRU",
            landingAirportCode = "XYZ"
        )

        val expected = FlightRouteFixture.nonExistingRoute
        assertEquals(expected, result)
        verify { flightRouteRepositoryMock.findAll() }
    }

    @Test
    fun `should return cached route if available`() {
        val cachedRoute = ShortestRouteAndPrice(
            totalPrice = 40,
            airportRoute = listOf("GRU", "BRC", "SCL", "ORL", "CDG")
        )

        impl.javaClass.getDeclaredField("serviceCache").apply {
            isAccessible = true
            (get(impl) as MutableMap<String, ShortestRouteAndPrice>)["GRU#CDG"] = cachedRoute
        }

        val result = impl.findCheapestTripAndRoute(
            takeOffAirportCode = "GRU",
            landingAirportCode = "CDG"
        )

        assertEquals(cachedRoute, result)
        verify(exactly = 0) { flightRouteRepositoryMock.findAll() }
    }
}