package br.com.ebury.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class FlightRouteDto(
    @JsonProperty("id")
    val id: String? = null,
    @JsonProperty("takeOffAirportCode")
    val takeOffAirportCode: String,
    @JsonProperty("landingAirportCode")
    val landingAirportCode: String,
    @JsonProperty("flightCostE2")
    val flightCostE2: Long,
)
