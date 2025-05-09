package br.com.ebury.model

import br.com.ebury.dto.FlightRouteDto
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "flightRoutes")
@CompoundIndexes(
    CompoundIndex(
        name = "unique_takeoff_landing",
        def = "{'takeOffAirportCode': 1, 'landingAirportCode': 1}",
        unique = true,
    ),
)
data class FlightRoute(
    @Id
    val id: String? = null,
    val takeOffAirportCode: String,
    val landingAirportCode: String,
    val flightCostE2: Long,
) {
    fun toDto(): FlightRouteDto {
        return FlightRouteDto(
            id = this.id,
            takeOffAirportCode = this.takeOffAirportCode,
            landingAirportCode = this.landingAirportCode,
            flightCostE2 = this.flightCostE2,
        )
    }
}

fun FlightRouteDto.toModel(): FlightRoute {
    return FlightRoute(
        id = this.id,
        takeOffAirportCode = this.takeOffAirportCode,
        landingAirportCode = this.landingAirportCode,
        flightCostE2 = this.flightCostE2,
    )
}
