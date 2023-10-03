package ru.practicum.sprint13koh8

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ServerApi {

    companion object {
        fun create(): ServerApi {
            return Retrofit.Builder()
                .baseUrl("https://raw.githubusercontent.com/avanisimov/sprint-13-koh-8/main/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ServerApi::class.java)
        }

    }

    @GET("data/catalog.json")
    fun getCatalog(): Call<CatalogResponse>

}

data class CatalogResponse(
    val items: List<CatalogItem>
)