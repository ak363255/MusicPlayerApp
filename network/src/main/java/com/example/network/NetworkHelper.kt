package com.example.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.IOException
import javax.inject.Inject

object NetworkHelper {
    lateinit var retrofitClient: RetrofitClient

    suspend inline fun <reified T:Any> makeGetRequest(
        url: String,
        header: Map<String, String> = mutableMapOf()
    ):NetworkResponse<T,Error> = getResponse(retrofitClient.apiService.makeGetRequest(url, header))


    suspend inline fun <reified T:Any> makePostRequest(
        url: String,
        body: JsonObject = JsonObject(),
        header: Map<String, String> = mutableMapOf()
    ): NetworkResponse<T, Error> =
        getResponse(retrofitClient.apiService.makePostRequest(url, body, header))


    suspend inline fun <reified T:Any> getResponse(callObject: NetworkResponse<String, Error>):NetworkResponse<T,Error> =
        when (callObject) {
            is NetworkResponse.Success -> {
                try {
                    NetworkResponse.Success(convert(callObject.body), 1)
                } catch (e: Exception) {
                    NetworkResponse.UnknownError(Error())
                }
            }
            is NetworkResponse.ApiError -> {
                NetworkResponse.ApiError(Error(),0)
            }
            is NetworkResponse.NetworkError -> {
                NetworkResponse.NetworkError(IOException(callObject.error.message))
            }
            is NetworkResponse.UnknownError -> {
                NetworkResponse.UnknownError(Error())
            }
        }

    inline fun <reified T> convert(response: String): T = gson.fromJson(response, T::class.java)
    inline fun <reified T>convertObjectToString(data:T):String? = gson.toJson(data)

    val gson by lazy {
        Gson()
    }
}