package com.tstreet.onhand.core.network.retrofit

import java.io.IOException

/**
 * We use covariant (out) because we want to return (produce) [T] and [U] and their subtypes (all
 * subtype of [Any], i.e. any class type).
 */
sealed class NetworkResponse<out T : Any, out U : Any> {

    /**
     * 2xx responses from API
     */
    data class Success<T : Any>(val body: T) : NetworkResponse<T, Nothing>()

    /**
     * Non-2xx responses from API
     */
    data class ApiError<U : Any>(val body: U, val code: Int) : NetworkResponse<Nothing, U>()

    /**
     * Non API related network issues, like no network connectivity
     */
    data class NetworkError(val error: IOException) : NetworkResponse<Nothing, Nothing>()

    /**
     * Unexpected exceptions while creating the request or processing the response, e.g.: parsing
     * issues
     */
    data class UnknownError(val error: Throwable?) : NetworkResponse<Nothing, Nothing>()
}
