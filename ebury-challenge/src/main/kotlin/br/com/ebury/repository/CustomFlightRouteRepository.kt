package br.com.ebury.repository

import br.com.ebury.model.FlightRoute
import org.springframework.stereotype.Repository

@Repository
interface CustomFlightRouteRepository {
    fun findExistingRoutes(flightRoutes: List<FlightRoute>): List<FlightRoute>
}
