package com.tstreet.onhand.core.network.retrofit

import com.tstreet.onhand.core.network.OnHandNetworkDataSource
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.tstreet.onhand.core.network.model.NetworkIngredient
import com.tstreet.onhand.core.network.model.NetworkIngredientSearchResult
import com.tstreet.onhand.core.network.model.NetworkRecipe
import com.tstreet.onhand.core.network.model.NetworkRecipeDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
import retrofit2.http.Path

private interface RetrofitOnHandService {

    //TODO: clean this up...

    // TODO: Unsubscribe here: https://rapidapi.com/developer/billing/subscriptions-and-usage and
    // generate new API key before making repo public. This is already in commit history...
    @GET("food/ingredients/search")
    fun getIngredients(
        @Query("query") prefix: String,
        // TODO: note, need to look into custom call adapter factory to make it so we don't have
        // to unfurl Call<> types...
    ): Call<NetworkIngredientSearchResult>

    // TODO: sort by number of likes to show more relevant recipes potentially
    @GET("recipes/findByIngredients")
    suspend fun getRecipesFromIngredients(
        @Query("ingredients") ingredients: List<String>,
        // TODO: note, need to look into custom call adapter factory to make it so we don't have
        // to unfurl Call<> types...
    ): List<NetworkRecipe>

    // TODO: sort by number of likes to show more relevant recipes potentially
    @GET("recipes/{id}/information")
    suspend fun getRecipeDetail(
        @Path("id") id: Int,
    ): NetworkRecipeDetail
}

private const val BASE_URL = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/"
private const val HOST = "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com"
private const val API_KEY = "3749218b77mshea638b2be581548p186f46jsn90edcd6e1d2c"

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
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("X-RapidAPI-Host", HOST)
                        .addHeader("X-RapidAPI-Key", API_KEY)
                    chain.proceed(request.build())
                }
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

    // TODO: is immediately returning a flow that emits the right way to handle this?
    // TODO: upstream can use flownOn(dispatcher) to change to appropriate coroutine context, may
    // want to take advantage of this later
    override fun getRecipesFromIngredients(ingredients: List<String>): Flow<List<NetworkRecipe>> {
        return flow { emit(networkApi.getRecipesFromIngredients(ingredients)) }
    }

    override fun getRecipeDetail(id: Int): Flow<NetworkRecipeDetail> {
        return flow { emit(networkApi.getRecipeDetail(id)) }
    }
}
