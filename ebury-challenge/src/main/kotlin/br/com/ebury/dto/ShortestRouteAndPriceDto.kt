package br.com.ebury.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ShortestRouteAndPriceDto(
    @JsonProperty("airportRoute")
    val airportRoute: List<String>,
    @JsonProperty("totalPrice")
    val totalPrice: Long,
)
