package com.example.musicplayerapp

import android.app.Application
import com.example.dbmodule.MusicDbHelper
import com.example.dbmodule.MusicRoomDb
import com.example.network.NetworkHelper
import com.example.network.RetrofitClient
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MusicPlayerApplication:Application() {
    @Inject
    lateinit var retrofitClient: RetrofitClient

    @Inject
    lateinit var musicRoomDb:MusicRoomDb


    override fun onCreate() {
        super.onCreate()
        applicationContext_ = this
        initializeRetrofit()
        initializeMusicDb()
    }

    private fun initializeMusicDb() {
         MusicDbHelper.musicDb = musicRoomDb
    }


    private fun initializeRetrofit() {
        NetworkHelper.retrofitClient = retrofitClient
    }

    companion object{
        private lateinit var applicationContext_:MusicPlayerApplication
        fun getApplicationContext() = applicationContext_
    }
}