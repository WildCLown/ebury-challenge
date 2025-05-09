package br.com.ebury.model

import br.com.ebury.dto.ShortestRouteAndPriceDto

data class ShortestRouteAndPrice(
    val airportRoute: List<String>,
    val totalPrice: Long,
) {
    fun toDto() =
        ShortestRouteAndPriceDto(
            airportRoute = airportRoute,
            totalPrice = totalPrice,
        )
}
