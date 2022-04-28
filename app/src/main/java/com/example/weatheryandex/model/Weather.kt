package com.example.weatheryandex.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class Weather(
    val city: City = getDefaultCity(),
    val temperature: Int = 0,
    val fellsLike: Int = 0,
    val condition: String = "Sunny",
): Parcelable

@Parcelize
data class City(
    val name: String,
    val lat: Double,
    val long: Double,
) : Parcelable

fun getDefaultCity() = City("Москва", 55.755826, 37.617299900000035)