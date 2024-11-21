package com.dicoding.asclepius.retrofit

import com.dicoding.asclepius.data.response.CancerResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines")
    suspend fun getAllArticles(
        @Query("q") query: String = "cancer",
        @Query("category") category: String = "health",
        @Query("language") language: String = "en",
        @Query("apiKey") apiKey: String
    ): CancerResponse
}
