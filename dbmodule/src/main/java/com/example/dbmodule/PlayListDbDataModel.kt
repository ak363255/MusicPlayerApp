package com.example.dbmodule

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "playlist_db_model")
data class PlayListDbDataModel(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val playListData: String
)