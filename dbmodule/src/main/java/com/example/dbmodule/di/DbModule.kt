package com.example.dbmodule.di

import android.content.Context
import androidx.room.Room
import com.example.dbmodule.MusicRoomDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {
    @Singleton
    @Provides
    fun provideRoomDb(@ApplicationContext context: Context):MusicRoomDb{
        return Room.databaseBuilder(context,MusicRoomDb::class.java,"MUSIC_DB").build()
    }
}