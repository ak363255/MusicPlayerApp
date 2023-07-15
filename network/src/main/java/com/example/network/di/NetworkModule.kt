package com.example.network.di

import com.example.network.ApiService
import com.example.network.NetworkResponseAdapterFactory
import com.example.network.OkHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providesScalarConvertorFactory(): ScalarsConverterFactory {
        return ScalarsConverterFactory.create()
    }
    @Singleton
    @Provides
    fun provideGsonConvertorFactory():GsonConverterFactory{
        return GsonConverterFactory.create()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient():okhttp3.OkHttpClient{
        return OkHttpClient.getOkhttpClientInterceptor()
    }

    @Provides
    @Singleton
    fun provideRetorfitInstance(
        okHttpClient: okhttp3.OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
        scalerConverterFactory: ScalarsConverterFactory,
    ):Retrofit{
           return  Retrofit.Builder()
               .client(okHttpClient)
               .baseUrl("https://google.com")
               .addCallAdapterFactory(NetworkResponseAdapterFactory())
               .addConverterFactory(scalerConverterFactory)
               .addConverterFactory(gsonConverterFactory)
               .build()
    }

    @Singleton
    @Provides
    fun provideApiServie(retrofit: Retrofit):ApiService{
      return retrofit.create(ApiService::class.java)
    }
}