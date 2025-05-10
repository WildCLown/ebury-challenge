package br.com.ebury.validations.controller

import br.com.ebury.dto.FlightRouteDto
import org.springframework.stereotype.Service

@Service
class StaticValidations {
    fun validateFlightRoute(
        takeOffAirportCode: String,
        landingAirportCode: String,
        flightCostE2: Long,
    ) {
        // NOTE: Not sure if it's in need to validate if 3 chars long, depends on products
        // This directly affects tests, as I've set a const fictitious airport code
        require(takeOffAirportCode.isNotBlank()) { "Take off airport code cannot be blank" }
        require(landingAirportCode.isNotBlank()) { "Landing airport code cannot be blank" }
        require(flightCostE2 >= 0) { "Flight cost must be greater or equals than zero" }
    }

    fun validateFlightRouteList(flightRouteList: List<FlightRouteDto>) {
        require(flightRouteList.isNotEmpty()) { "Flight route list cannot be empty" }
        val flightRouteSet = mutableSetOf<String>()
        flightRouteList.forEach { flightRoute ->
            val uniqueFlight = "${flightRoute.takeOffAirportCode}#${flightRoute.landingAirportCode}"
            if (flightRouteSet.contains(uniqueFlight)) {
                throw IllegalArgumentException("Flight $uniqueFlight is repeated in request")
            }
            flightRouteSet.add(uniqueFlight)
            validateFlightRoute(
                takeOffAirportCode = flightRoute.takeOffAirportCode,
                landingAirportCode = flightRoute.landingAirportCode,
                flightCostE2 = flightRoute.flightCostE2,
            )
        }
    }
}
