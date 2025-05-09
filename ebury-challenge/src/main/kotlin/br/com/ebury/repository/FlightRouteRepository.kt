package br.com.ebury.repository

import br.com.ebury.model.FlightRoute
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface FlightRouteRepository : MongoRepository<FlightRoute, String>, CustomFlightRouteRepository {
    fun findByTakeOffAirportCodeAndLandingAirportCode(
        takeOffAirportCode: String,
        landingAirportCode: String,
    ): FlightRoute?
}
