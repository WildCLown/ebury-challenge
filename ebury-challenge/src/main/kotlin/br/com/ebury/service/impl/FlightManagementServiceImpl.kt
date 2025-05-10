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

@Service
class FlightManagementServiceImpl(
    private val flightRouteRepository: FlightRouteRepository,
) : FlightManagementService {
    private val serviceCache = mutableMapOf<String, ShortestRouteAndPrice>()

    override fun purgeFlightRoutes() {
        flightRouteRepository.deleteAll()
    }

    override fun upsertFlightRoutes(flightRoutesDto: List<FlightRouteDto>): List<String> {
//        NOTE: If willing to mantain CSV behavior, that by setting the file, will start using it...
//        purgeFlightRoutes()
        val flightRoutes =
            flightRoutesDto.map {
                FlightRoute(
                    id = null,
                    takeOffAirportCode = it.takeOffAirportCode,
                    landingAirportCode = it.landingAirportCode,
                    flightCostE2 = it.flightCostE2,
                )
            }

        val saveList = mutableListOf<FlightRoute>()
        val existingFlightRoutes = flightRouteRepository.findExistingRoutes(flightRoutes)
        val existingFlightMap = buildFlightRouteMap(existingFlightRoutes)
        val responseIds = mutableListOf<String>()

        flightRoutes.forEach {
            val existingRoute = existingFlightMap["${it.takeOffAirportCode}#${it.landingAirportCode}"]

            if (existingRoute == null) {
                val generatedId = ObjectId().toHexString()
                val flightRoute = it.copy(id = generatedId)
                responseIds.add(generatedId)
                saveList.add(flightRoute)
            } else {
                responseIds.add(existingRoute.id!!)
                if (existingRoute.flightCostE2 > it.flightCostE2) {
                    val flightRoute = existingRoute.copy(flightCostE2 = it.flightCostE2)
                    saveList.add(flightRoute)
                } else {
                    logger.debug(
                        "Didn't insert new route, as it is more expensive, validate with products the behavior",
                    )
                }
            }
        }

        // NOTE: Depending on behaviors, could use transactional
        // But as we are saving as batch, or everything will be saved or nothing
        if (saveList.isNotEmpty()) {
            serviceCache.clear()
            flightRouteRepository.saveAll(saveList)
        }

        return responseIds
    }

    override fun findCheapestTripAndRoute(
        takeOffAirportCode: String,
        landingAirportCode: String,
    ): ShortestRouteAndPrice {
        // TODO: Not the ideal, should use cache system such as REDIS or other memory cache tool!!
        logger.info("Service cache size: ${serviceCache.size}")
        val cachedTrip = serviceCache["$takeOffAirportCode#$landingAirportCode"]
        if (cachedTrip != null) {
            logger.info("Returning cached trip: $cachedTrip")
            return cachedTrip
        }
        val allFlights = flightRouteRepository.findAll()
        val checkedFlights = mutableSetOf<String>()
        val pricedAdjacencyList = buildPricedAdjacencyList(allFlights)

        val dijkstraQueue = PriorityQueue<ShortestRouteAndPrice>(compareBy { it.totalPrice })
        val initialRoute =
            ShortestRouteAndPrice(
                totalPrice = 0,
                airportRoute = listOf(takeOffAirportCode),
            )

        insertItemsInShortenPath(
            pricedAdjacencyList,
            dijkstraQueue,
            checkedFlights,
            initialRoute,
            landingAirportCode,
        )?.let { return it }

        while (!dijkstraQueue.isEmpty()) {
            val shortestNonFinalRoute = dijkstraQueue.poll()
            insertItemsInShortenPath(
                pricedAdjacencyList,
                dijkstraQueue,
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
        val foundPath = "${currentShortestRoute.airportRoute.first()}#$currentAirportCode"
        serviceCache[foundPath] =
            ShortestRouteAndPrice(
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
