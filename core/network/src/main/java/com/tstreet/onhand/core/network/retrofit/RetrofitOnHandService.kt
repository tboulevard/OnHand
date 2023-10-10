package com.tstreet.onhand.core.network.retrofit

import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.tstreet.onhand.core.network.model.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import retrofit2.http.Path

private interface RetrofitOnHandService {

    // TODO: Unsubscribe here: https://rapidapi.com/developer/billing/subscriptions-and-usage and
    // generate new API key before making repo public. This is already in commit history...
    @GET("food/ingredients/search")
    suspend fun getIngredients(
        @Query("query") prefix: String,
    ): OnHandNetworkResponse<NetworkIngredientSearchResult>

    // TODO: sort by number of likes to show more relevant recipes potentially
    @GET("recipes/findByIngredients")
    suspend fun getRecipesFromIngredients(
        @Query("ingredients") ingredients: List<String>,
    ): OnHandNetworkResponse<List<NetworkRecipe>>

    @GET("recipes/{id}/information")
    suspend fun getRecipeDetail(
        @Path("id") id: Int,
    ): OnHandNetworkResponse<NetworkRecipeDetail>
}

@Singleton
class RetrofitOnHandNetwork @Inject constructor(
    networkJson: Json,
    baseUrl: String,
    host: String,
    apiKey: String
) : OnHandNetworkDataSource {

    private val networkApi = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory(NetworkResponseAdapterFactory())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(
                    // TODO: Decide logging logic
                    HttpLoggingInterceptor().apply {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    }
                )
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("X-RapidAPI-Host", host)
                        .addHeader("X-RapidAPI-Key", apiKey)
                    chain.proceed(request.build())
                }
                .build()
        )
        .addConverterFactory(
            networkJson.asConverterFactory("application/json".toMediaType())
        )
        .build()
        .create(RetrofitOnHandService::class.java)

    override suspend fun getIngredients(
        prefix: String
    ): OnHandNetworkResponse<NetworkIngredientSearchResult> {
        val response = networkApi.getIngredients(prefix)
        logInfo(response)
        return response
    }

    override suspend fun findRecipesFromIngredients(
        ingredients: List<String>
    ): OnHandNetworkResponse<List<NetworkRecipe>> {
        val response = networkApi.getRecipesFromIngredients(ingredients)
        logInfo(response)
        return response
    }

    override suspend fun getRecipeDetail(
        id: Int
    ): OnHandNetworkResponse<NetworkRecipeDetail> {
        val response = networkApi.getRecipeDetail(id)
        logInfo(response)
        return response
    }

    private fun <T : Any> logInfo(response: OnHandNetworkResponse<T>) {
        return when (response) {
            is NetworkResponse.Success -> {
                println("[OnHand] Success: " + response.body)
            }
            is NetworkResponse.ApiError -> {
                println("[OnHand] ApiError: HTTP ${response.code} error: " + response.body)
            }
            is NetworkResponse.NetworkError -> {
                println("[OnHand] NetworkError: Device does not appear to have network connectivity.")
            }
            is NetworkResponse.UnknownError -> {
                println("[OnHand] Unknown non-network error: " + response.error?.message)
            }
        }
    }
}
