package com.example.dbmodule

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MusicDao {
    @Query("SELECT * FROM music_db_model")
    suspend fun getMusicList():List<MusicDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMusicItem(musicDbModel: MusicDbModel)

    @Query("SELECT * FROM music_db_model WHERE :id = id")
    suspend fun getMusic(id:String):List<MusicDbModel>

    @Query("DELETE FROM music_db_model WHERE :id = id")
    suspend fun deleteFavMusicById(id:String)
}