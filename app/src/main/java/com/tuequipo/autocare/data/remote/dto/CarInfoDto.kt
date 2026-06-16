// data/remote/dto/CarInfoDto.kt
package com.tuequipo.autocare.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CarInfoDto(
    @SerializedName("make") val make: String,
    @SerializedName("model") val model: String,
    @SerializedName("year") val year: Int,
    @SerializedName("cylinders") val cylinders: Int,
    @SerializedName("fuel_type") val fuelType: String,
    @SerializedName("city_mpg") val cityMpg: Int,
    @SerializedName("highway_mpg") val highwayMpg: Int
)
