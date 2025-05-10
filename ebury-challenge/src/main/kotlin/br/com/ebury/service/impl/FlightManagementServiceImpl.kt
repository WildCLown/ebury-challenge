package br.com.ebury.service.impl

import br.com.ebury.dto.FlightRouteDto
import br.com.ebury.model.FlightRoute
import br.com.ebury.model.ShortestRouteAndPrice
import br.com.ebury.repository.FlightRouteRepository
import br.com.ebury.service.FlightManagementService
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.PriorityQueue
import kotlin.math.log

@Service
class FlightManagementServiceImpl(
    private val flightRouteRepository: FlightRouteRepository,
) : FlightManagementService {
    private val serviceCache = mutableMapOf<String, ShortestRouteAndPrice>()

    override fun purgeFlightRoutes() {
        flightRouteRepository.deleteAll()
    }

    override fun upsertFlightRoutes(flightRoutesDto: List<FlightRouteDto>): List<String> {
        val flightRoutes =
            flightRoutesDto.map {
                FlightRoute(
                    id = null,
                    takeOffAirportCode = it.takeOffAirportCode,
                    landingAirportCode = it.landingAirportCode,
                    flightCostE2 = it.flightCostE2,
                )
            }

        val existingFlightRoutes = flightRouteRepository.findExistingRoutes(flightRoutes)
        val existingFlightMap = buildFlightRouteMap(existingFlightRoutes)

        val responseIds = mutableListOf<String>()

        // TODO Make transactional
        flightRoutes.forEach {
            val existingRoute = existingFlightMap["${it.takeOffAirportCode}#${it.landingAirportCode}"]

            if (existingRoute == null) {
                val generatedId = ObjectId().toHexString()
                val flightRoute = it.copy(id = generatedId)
                flightRouteRepository.save(flightRoute)
            } else {
                responseIds.add(existingRoute.id!!)
                if (existingRoute.flightCostE2 > it.flightCostE2) {
                    val flightRoute = existingRoute.copy(flightCostE2 = it.flightCostE2)
                    flightRouteRepository.save(flightRoute)
                }
            }
        }

        //TODO: By saving as batch, will know if need to update itens
        // If I won't update cause I already have smallest route, and won't insert any
        // Don't need to clear cache
        serviceCache.clear()
        // TODO: Save as batch not in loop

        return responseIds
    }

    override fun findCheapestTripAndRoute(
        takeOffAirportCode: String,
        landingAirportCode: String,
    ): ShortestRouteAndPrice {
        //TODO: Not the ideal, should use cache system such as REDIS or other memory cache tool!!
        logger.info("Service cache size: ${serviceCache.size}")
        val cachedTrip = serviceCache["$takeOffAirportCode#$landingAirportCode"]
        if (cachedTrip != null) {
            logger.info("Returning cached trip: $cachedTrip")
            return cachedTrip
        }
        val allFlights = flightRouteRepository.findAll()
        val checkedFlights = mutableSetOf<String>()
        val pricedAdjacencyList = buildPricedAdjacencyList(allFlights)

        val djkstraQueue = PriorityQueue<ShortestRouteAndPrice>(compareBy { it.totalPrice })
        val initialRoute = ShortestRouteAndPrice(
            totalPrice = 0,
            airportRoute = listOf(takeOffAirportCode),
        )

        insertItemsInShortenPath(
            pricedAdjacencyList,
            djkstraQueue,
            checkedFlights,
            initialRoute,
            landingAirportCode,
        )?.let { return it }

        while (!djkstraQueue.isEmpty()) {
            val shortestNonFinalRoute = djkstraQueue.poll()
            insertItemsInShortenPath(
                pricedAdjacencyList,
                djkstraQueue,
                checkedFlights,
                shortestNonFinalRoute,
                landingAirportCode,
            )?.let { return it }
        }

        return ShortestRouteAndPrice(
            totalPrice = -1,
            airportRoute = emptyList(),
        )
    }

    private fun buildFlightRouteMap(flightRoutes: List<FlightRoute>): Map<String, FlightRoute> {
        return flightRoutes.associateBy { "${it.takeOffAirportCode}#${it.landingAirportCode}" }
    }

    private fun buildPricedAdjacencyList(flightRoutes: List<FlightRoute>): Map<String, List<Pair<String, Long>>> {
        val pricedAdjacencyList = mutableMapOf<String, MutableList<Pair<String, Long>>>()
        flightRoutes.forEach { flightRoute ->
            pricedAdjacencyList.computeIfAbsent(flightRoute.takeOffAirportCode) { mutableListOf() }
                .add(Pair(flightRoute.landingAirportCode, flightRoute.flightCostE2))
        }

        return pricedAdjacencyList
    }

    private fun insertItemsInShortenPath(
        pricedAdjacencyList: Map<String, List<Pair<String, Long>>>,
        djkstraQueue: PriorityQueue<ShortestRouteAndPrice>,
        checkedFlights: MutableSet<String>,
        currentShortestRoute: ShortestRouteAndPrice,
        targetAirportCode: String,
    ): ShortestRouteAndPrice? {
        val currentAirportCode = currentShortestRoute.airportRoute.last()

        if (checkedFlights.contains(currentAirportCode)) {
            return null
        }
        checkedFlights.add(currentAirportCode)
        val foundPath = "${currentShortestRoute.airportRoute.first()}#${currentAirportCode}"
        serviceCache[foundPath] = ShortestRouteAndPrice(
            totalPrice = currentShortestRoute.totalPrice,
            airportRoute = currentShortestRoute.airportRoute,
        )

        if (currentAirportCode == targetAirportCode) {
            return ShortestRouteAndPrice(
                totalPrice = currentShortestRoute.totalPrice,
                airportRoute = currentShortestRoute.airportRoute,
            )
        }

        pricedAdjacencyList[currentAirportCode]?.forEach { (nextAirportCode, price) ->
            if (!checkedFlights.contains(nextAirportCode)) {
                val newRoute =
                    ShortestRouteAndPrice(
                        totalPrice = currentShortestRoute.totalPrice + price,
                        airportRoute = currentShortestRoute.airportRoute + nextAirportCode,
                    )
                djkstraQueue.add(newRoute)
            }
        }
        return null
    }

    companion object {
        private val logger = LoggerFactory.getLogger(FlightManagementServiceImpl::class.java)
    }
}
