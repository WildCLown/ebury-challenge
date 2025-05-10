package br.com.ebury.fixtures

import br.com.ebury.dto.FlightRouteDto

object FlightRouteDtoFixture {
    val flightRoute =
        FlightRouteDto(
            takeOffAirportCode = "LAX",
            landingAirportCode = "JFK",
            flightCostE2 = 1000,
        )

    val listFlightRoute =
        mutableListOf(
            flightRoute,
            FlightRouteDto(
                takeOffAirportCode = "JFK",
                landingAirportCode = "LAX",
                flightCostE2 = 1000,
            ),
            FlightRouteDto(
                takeOffAirportCode = "LAX",
                landingAirportCode = "ORD",
                flightCostE2 = 2000,
            ),
        )
}
