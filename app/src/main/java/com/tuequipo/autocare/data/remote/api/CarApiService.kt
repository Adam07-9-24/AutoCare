// data/remote/api/CarApiService.kt
package com.tuequipo.autocare.data.remote.api

import com.tuequipo.autocare.data.remote.dto.CarInfoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CarApiService {
    @GET("cars")
    suspend fun getCarInfo(
        @Query("make") make: String,
        @Query("model") model: String
    ): List<CarInfoDto>
}
