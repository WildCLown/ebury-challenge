package br.com.ebury.controller

import br.com.ebury.dto.FlightRouteDto
import br.com.ebury.dto.ShortestRouteAndPriceDto
import br.com.ebury.service.FlightManagementService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/flight")
class FlightManagementController(
    private val flightManagementService: FlightManagementService,
) {
    @PostMapping("/list")
    fun upsertFlightRoutes(
        @RequestBody request: List<FlightRouteDto>,
    ): List<String> {
        // TODO: Validate request
        try {
            // If willing to mantain CSV behavior, that by setting the file, will start using it...
            // flightManagementService.purgeFlightRoutes()
            return flightManagementService.upsertFlightRoutes(
                flightRoutesDto = request,
            )
        } catch (ex: Exception) {
            logger.info("Error creating flight route: ${ex.message}")
            throw ex
        }
    }

    @GetMapping("/cheapest-trip")
    fun findCheapestTripAndRoute(
        @RequestParam takeOffAirportCode: String,
        @RequestParam landingAirportCode: String,
    ): ShortestRouteAndPriceDto {
        try {
            val cheapestTripAndRoute =
                flightManagementService.findCheapestTripAndRoute(
                    takeOffAirportCode = takeOffAirportCode,
                    landingAirportCode = landingAirportCode,
                )

            return cheapestTripAndRoute.toDto()
        } catch (ex: Exception) {
            logger.info("Error finding cheapest trip: ${ex.message}")
            throw ex
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(FlightManagementController::class.java)
    }
}
