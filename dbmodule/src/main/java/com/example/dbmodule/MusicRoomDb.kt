package com.example.dbmodule

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [MusicDbModel::class,PlayListDbDataModel::class],
    version = 1,
    exportSchema = false
)
abstract class MusicRoomDb :RoomDatabase(){
    abstract fun getMusicDao():MusicDao
    abstract fun getPlayListDao():PlayListDao
}