package br.com.ebury.validations.staticValidations

import br.com.ebury.dto.FlightRouteDto
import br.com.ebury.fixtures.FlightRouteDtoFixture.flightRoute
import br.com.ebury.fixtures.FlightRouteDtoFixture.listFlightRoute
import br.com.ebury.validations.controller.StaticValidations
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ValidateFlightRouteListTest {
    private val staticValidations = StaticValidations()

    @Test
    fun `happy path`() {
        staticValidations.validateFlightRouteList(listFlightRoute)
    }

    @Test
    fun `should throw exception when flight route list is empty`() {
        val emptyList = emptyList<FlightRouteDto>()

        val exception =
            assertThrows<IllegalArgumentException> {
                staticValidations.validateFlightRouteList(emptyList)
            }
        assert(exception.message == "Flight route list cannot be empty")
    }

    @Test
    fun `should throw exception when flight route is repeated`() {
        val repeatedList = listFlightRoute + flightRoute

        val exception =
            assertThrows<IllegalArgumentException> {
                staticValidations.validateFlightRouteList(repeatedList)
            }
        assert(exception.message == "Flight LAX#JFK is repeated in request")
    }

    @Test
    fun `should throw exception when takeOffAirportCode is blank`() {
        val invalidList =
            listOf(
                flightRoute.copy(takeOffAirportCode = ""),
            )

        val exception =
            assertThrows<IllegalArgumentException> {
                staticValidations.validateFlightRouteList(invalidList)
            }
        assert(exception.message == "Take off airport code cannot be blank")
    }

    @Test
    fun `should throw exception when any landingAirportCode is blank`() {
        val invalidList = listFlightRoute + flightRoute.copy(landingAirportCode = "")

        val exception =
            assertThrows<IllegalArgumentException> {
                staticValidations.validateFlightRouteList(invalidList)
            }
        assert(exception.message == "Landing airport code cannot be blank")
    }

    @Test
    fun `should throw exception when flightCostE2 is less than zero`() {
        val invalidList =
            listFlightRoute +
                flightRoute.copy(
                    landingAirportCode = testAirportCode,
                    flightCostE2 = -1
                )

        val exception = assertThrows<IllegalArgumentException> {
                staticValidations.validateFlightRouteList(invalidList)
            }
        assert(exception.message == "Flight cost must be greater or equals than zero")
    }

    companion object {
        private const val testAirportCode = "UNIQUE_TEST_VALUE"
    }
}
