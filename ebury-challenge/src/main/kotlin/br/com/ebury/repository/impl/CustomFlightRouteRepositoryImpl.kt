package br.com.ebury.repository.impl

import br.com.ebury.model.FlightRoute
import br.com.ebury.repository.CustomFlightRouteRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

class CustomFlightRouteRepositoryImpl(
    private val mongoTemplate: MongoTemplate,
) : CustomFlightRouteRepository {
    override fun findExistingRoutes(flightRoutes: List<FlightRoute>): List<FlightRoute> {
        val criteria =
            flightRoutes.map {
                Criteria().andOperator(
                    Criteria.where("takeOffAirportCode").`is`(it.takeOffAirportCode),
                    Criteria.where("landingAirportCode").`is`(it.landingAirportCode),
                )
            }

        val query = Query().addCriteria(Criteria().orOperator(*criteria.toTypedArray()))
        return mongoTemplate.find(query, FlightRoute::class.java)
    }
}
