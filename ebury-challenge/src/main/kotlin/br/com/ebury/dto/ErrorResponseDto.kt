package br.com.ebury.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ErrorResponseDto(
    @JsonProperty("message")
    val message: String,
    @JsonProperty("status")
    val status: Int,
)
