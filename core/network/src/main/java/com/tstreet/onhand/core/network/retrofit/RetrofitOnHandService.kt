package com.tstreet.onhand.core.network.retrofit

import com.tstreet.onhand.core.network.model.NetworkIngredient
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json

private interface RetrofitOnHandService {
    @GET("food/ingredients/search")
    fun getIngredients(
        @Query("query") prefix: String,
    ): NetworkResponse<List<NetworkIngredient>>
}

private const val BASE_URL = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/"

/**
 * Wrapper for data provided from the [BASE_URL]
 */
@Serializable
private data class NetworkResponse<T>(
    val data: T
)

@Singleton
class RetrofitNiaNetwork @Inject constructor(
    networkJson: Json
) {

    private val networkApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(
            OkHttpClient.Builder()
                .addInterceptor(
                    // TODO: Decide logging logic
                    HttpLoggingInterceptor().apply {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    }
                )
                .build()
        )
        .addConverterFactory(
            @OptIn(ExperimentalSerializationApi::class)
            networkJson.asConverterFactory("application/json".toMediaType())
        )
        .build()
        .create(RetrofitOnHandService::class.java)

    fun getIngredients(prefix: String): List<NetworkIngredient> {
        // TODO:
        return listOf()
    }
}