package br.com.ebury.fixtures

import br.com.ebury.model.FlightRoute
import br.com.ebury.model.ShortestRouteAndPrice

object FlightRouteFixture {
    val nonExistingRoute = ShortestRouteAndPrice(
        totalPrice = -1,
        airportRoute = emptyList()
    )

    val listFlights = listOf(
        FlightRoute("64b8f1f1e4b0a1a1a1a1a1a1", "GRU", "BRC", 10),
        FlightRoute("64b8f1f1e4b0a1a1a1a1a1a2", "BRC", "SCL", 5),
        FlightRoute("64b8f1f1e4b0a1a1a1a1a1a3", "GRU", "CDG", 75),
        FlightRoute("64b8f1f1e4b0a1a1a1a1a1a4", "GRU", "SCL", 20),
        FlightRoute("64b8f1f1e4b0a1a1a1a1a1a5", "GRU", "ORL", 56),
        FlightRoute("64b8f1f1e4b0a1a1a1a1a1a6", "ORL", "CDG", 5),
        FlightRoute("64b8f1f1e4b0a1a1a1a1a1a7", "SCL", "ORL", 20)
    )
}