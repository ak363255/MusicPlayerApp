package com.example.dbmodule

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_db_model")
data class MusicDbModel(
    @PrimaryKey(autoGenerate = false)
    var id:String,
    var musicData:String
)
