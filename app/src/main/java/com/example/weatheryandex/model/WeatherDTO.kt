package com.example.weatheryandex.model

data class WeatherDTO(
    var fact: FactDTO?
)

data class FactDTO(
    val temp: Int?,
    val feels_like: Int?,
    val condition: String?,
)
