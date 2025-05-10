package br.com.ebury.validations.staticValidations

import br.com.ebury.validations.controller.StaticValidations
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ValidateFlightRouteTest {
    private val staticValidations = StaticValidations()

    @Test
    fun `happy path`() {
        assertDoesNotThrow {
            staticValidations.validateFlightRoute(
                takeOffAirportCode = "ABC",
                landingAirportCode = "DEF",
                flightCostE2 = 100,
            )
        }
    }

    @Test
    fun `should throw exception when takeOffAirportCode is blank`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                staticValidations.validateFlightRoute(
                    takeOffAirportCode = "",
                    landingAirportCode = "DEF",
                    flightCostE2 = 100,
                )
            }
        assert(exception.message == "Take off airport code cannot be blank")
    }

    @Test
    fun `should throw exception when landingAirportCode is blank`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                staticValidations.validateFlightRoute(
                    takeOffAirportCode = "ABC",
                    landingAirportCode = "",
                    flightCostE2 = 100,
                )
            }
        assert(exception.message == "Landing airport code cannot be blank")
    }

    @Test
    fun `should throw exception when flightCostE2 is less than zero`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                staticValidations.validateFlightRoute(
                    takeOffAirportCode = "ABC",
                    landingAirportCode = "DEF",
                    flightCostE2 = -1,
                )
            }
        assert(exception.message == "Flight cost must be greater or equals than zero")
    }
}
