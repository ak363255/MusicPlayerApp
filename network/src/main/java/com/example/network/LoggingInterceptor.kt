package com.example.network

import okhttp3.logging.HttpLoggingInterceptor

class LoggingInterceptor {
    companion object{
        private val loggingInterceptor:HttpLoggingInterceptor by lazy {HttpLoggingInterceptor()}
        fun getLoggigInterceptor():HttpLoggingInterceptor{
            if(BuildConfig.DEBUG){
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            }else{
                loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
            }
            return loggingInterceptor
        }
    }
}