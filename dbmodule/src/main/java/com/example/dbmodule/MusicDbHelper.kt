package com.example.dbmodule

import com.example.musicplayerapp.domain.model.Song
import com.example.network.NetworkHelper

object MusicDbHelper {
     lateinit var  musicDb:MusicRoomDb

    suspend fun insertMusicData(music: Song){
          val jsonString = NetworkHelper.convertObjectToString(music)
          jsonString?.let {songData  ->
              val musicDbModel = MusicDbModel(musicData = songData, id = music.id?:"")
              if(this::musicDb.isInitialized){
                  musicDb.getMusicDao().insertMusicItem(musicDbModel)
              }
          }
    }

    suspend fun getAllSongs():List<Song>{
        val songList = mutableListOf<Song>()
        if(this::musicDb.isInitialized){
            val musicDataModel = musicDb.getMusicDao().getMusicList()
            musicDataModel.forEach {
                val song = NetworkHelper.convert<Song?>(it.musicData)
                song?.let {
                    songList.add(it)
                }
            }
        }
        return songList
    }

    suspend fun getMusicById(id:String):Song?{
        var song:Song? = null
        if(this::musicDb.isInitialized){
            val musicDbModel = musicDb.getMusicDao().getMusic(id)
            musicDbModel.let {
                it.forEach {
                     song = NetworkHelper.convert(it.musicData)
                }
            }
        }
       return song
    }

    suspend fun removeFromFavMusiceById(id:String){
        if(this::musicDb.isInitialized){
            musicDb.getMusicDao().deleteFavMusicById(id)
        }
    }
}