package com.tstreet.onhand.core.common

import com.tstreet.onhand.core.common.Status.ERROR
import com.tstreet.onhand.core.common.Status.SUCCESS

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T? = null): Resource<T> {
            return Resource(ERROR, data, msg)
        }
    }
}

enum class Status {
    SUCCESS,
    ERROR
}
