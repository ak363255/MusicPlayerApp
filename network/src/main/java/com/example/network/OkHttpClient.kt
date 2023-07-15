package com.example.network

import okhttp3.Headers
import java.util.concurrent.TimeUnit

class OkHttpClient private constructor() {

    companion object {
        private var networkConfiguration: NetworkConfiguration = NetworkConfiguration.Builder()
            .build()

        fun setNetworkConfiguration(networkConfiguration: NetworkConfiguration) {
             this.networkConfiguration = networkConfiguration
        }
        fun getOkhttpClientInterceptor():okhttp3.OkHttpClient{
            return okhttp3.OkHttpClient.Builder()
                .addInterceptor{
                  val newRequest = it.request().newBuilder()
                    networkConfiguration.baseHeader.forEach { key, value ->
                        newRequest.addHeader(key,value)
                    }
                    newRequest.addHeader("Content-Type", "application/json")
                    it.proceed(newRequest.build())
                }
                .addInterceptor(LoggingInterceptor.getLoggigInterceptor())
                .connectTimeout(networkConfiguration.connectionTimeOut,TimeUnit.MILLISECONDS)
                .readTimeout(networkConfiguration.readTimeOut,TimeUnit.MILLISECONDS)
                .readTimeout(networkConfiguration.connectionTimeOut,TimeUnit.MILLISECONDS)
                .build()
        }
    }
}