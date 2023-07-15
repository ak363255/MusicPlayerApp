package com.example.network

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*
import kotlin.reflect.KClass

interface ApiService {

    @GET
   suspend fun makeGetRequest(@Url url:String,@HeaderMap header:Map<String,String> = mutableMapOf()):NetworkResponse<String,Error>

    @POST
    suspend fun makePostRequest(@Url url:String,@Body body:JsonObject,@HeaderMap header:Map<String,String> = mutableMapOf()):NetworkResponse<String,Error>
}