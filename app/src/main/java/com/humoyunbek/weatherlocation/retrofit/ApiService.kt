package com.humoyunbek.weatherlocation.retrofit

import com.humoyunbek.weatherlocation.model.Data
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("weather")
    fun getData(
        @Query("lat") lat:String,
        @Query("lon") lon:String,
        @Query("units") units:String,
        @Query("appid") appid:String
    ):Call<Data>
}