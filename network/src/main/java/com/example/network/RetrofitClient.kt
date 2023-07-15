package com.example.network

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Inject

class RetrofitClient @Inject constructor(val apiService: ApiService)