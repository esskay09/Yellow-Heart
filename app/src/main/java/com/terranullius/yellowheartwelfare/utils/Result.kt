package com.terranullius.yellowheartwelfare.utils

sealed class Result<out R> {

    data class Success<out T>(val data: T ) : Result<T>()
    data class Error<out T>(val exception: Exception? = null, val data: T? = null) : Result<T>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error<*> -> "Error[data=$data]"
            Loading -> "Loading"
        }
    }
}