package com.example.network


sealed class ResultEvent<out R> {
    data class OnSuccess<out T>(val data:T):ResultEvent<T>()
    data class OnFailure( val msg:kotlin.String):ResultEvent<Nothing>()
    object OnLoading:ResultEvent<Nothing>()
}
