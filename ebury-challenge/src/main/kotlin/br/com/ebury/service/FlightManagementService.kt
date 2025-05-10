package br.com.ebury.service

import br.com.ebury.dto.FlightRouteDto
import br.com.ebury.model.ShortestRouteAndPrice

interface FlightManagementService {
    fun purgeFlightRoutes()

    fun upsertFlightRoutes(
        flightRoutesDto: List<FlightRouteDto>
    ): List<String>

    fun findCheapestTripAndRoute(
        takeOffAirportCode: String,
        landingAirportCode: String,
    ): ShortestRouteAndPrice
}
