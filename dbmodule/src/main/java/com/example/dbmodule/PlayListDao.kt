package com.example.dbmodule

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlayListDao {
    @Query("SELECT * FROM playlist_db_model")
    suspend fun getPlayList():List<PlayListDbDataModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayListItem(musicDbModel: PlayListDbDataModel)

    @Query("SELECT * FROM playlist_db_model WHERE id = :id")
    suspend fun getPlayList(id:String):List<PlayListDbDataModel>

    @Query("DELETE FROM playlist_db_model WHERE id = :id")
    suspend fun deletePlayListById(id:Int)

    @Query("UPDATE playlist_db_model SET playListData = :data WHERE id = :id")
    suspend fun updatePlayList(id:Int,data:String)
}