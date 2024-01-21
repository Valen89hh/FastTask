package com.valendev.fasttask.resource

sealed class Status<out T>{
    data class Success<T>(val data: T): Status<T>()
    data class Error(val message: String): Status<Nothing>()
}
