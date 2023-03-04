package com.tstreet.onhand.core.network.retrofit

import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.tstreet.onhand.core.network.model.NetworkIngredient
import com.tstreet.onhand.core.network.model.IngredientSearchResult
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
import retrofit2.Call
import retrofit2.http.Headers

private interface RetrofitOnHandService {

    //TODO: clean this up...

    // TODO: Unsubscribe here: https://rapidapi.com/developer/billing/subscriptions-and-usage and
    // generate new API key before making repo public. This is already in commit history...
    @Headers(
        "X-RapidAPI-Host: spoonacular-recipe-food-nutrition-v1.p.rapidapi.com",
        "X-RapidAPI-Key: 3749218b77mshea638b2be581548p186f46jsn90edcd6e1d2c"
    )
    @GET("food/ingredients/search")
    fun getIngredients(
        @Query("query") prefix: String,
        // TODO: note, need to look into custom call adapter factory to make it so we don't have
        // to unfurl Call<> types...
    ): Call<IngredientSearchResult>
}

private const val BASE_URL = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/"

/**
 * Wrapper for data provided from the [BASE_URL]
 *
 * TODO: for some reason wrapping this around the [RetrofitOnHandService] throws an error...
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
        // TODO: oof...refactor later...
        return networkApi.getIngredients(prefix).execute().body()!!.results
    }
}
