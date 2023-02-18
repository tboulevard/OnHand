package com.tstreet.onhand.core.network.retrofit

import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
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
import kotlinx.serialization.json.Json

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
class RetrofitOnHandNetwork @Inject constructor(
    networkJson: Json
) : OnHandNetworkDataSource {

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
            // TODO: deep dive why we need the serialization api
            @OptIn(ExperimentalSerializationApi::class)
            networkJson.asConverterFactory("application/json".toMediaType())
        )
        .build()
        .create(RetrofitOnHandService::class.java)

    override fun getIngredients(prefix: String): List<NetworkIngredient> {
        return networkApi.getIngredients(prefix).data
    }
}