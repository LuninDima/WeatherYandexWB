package com.example.weatheryandex.model

data class WeatherDTO(
    var fact: FactDTO?
)

data class FactDTO(
    var temp: Int?,
    var feels_like: Int?,
    var condition: String?,
)
