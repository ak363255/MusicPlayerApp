package com.example.dbmodule

import com.example.musicplayerapp.domain.model.Song
import com.example.network.NetworkHelper
import com.example.recipecore.domain.model.PlayListModel

object PlayListDbHelper {
    private val musicDb by lazy {
        MusicDbHelper.musicDb
    }

    suspend fun insertPlayListData(playList: PlayListModel) {
        val jsonString = NetworkHelper.convertObjectToString(playList)
        jsonString?.let { songData ->
            val musicDbModel =
                PlayListDbDataModel(playListData = songData)
            musicDb.getPlayListDao().insertPlayListItem(musicDbModel)
        }
    }

    suspend fun getAllPlayList(): List<PlayListModel> {
        val songList = mutableListOf<PlayListModel>()
        val musicDataModel = musicDb.getPlayListDao().getPlayList()
        musicDataModel.forEach {
            val song = NetworkHelper.convert<PlayListModel?>(it.playListData)
            song?.id = it.id
            song?.let {
                songList.add(it)
            }
        }

        return songList
    }

    suspend fun getPlayListById(id: String): PlayListModel? {
        var song: PlayListModel? = null
        val musicDbModel = musicDb.getPlayListDao().getPlayList(id)
        musicDbModel.let {
            it.forEach {
                song = NetworkHelper.convert(it.playListData)
            }

        }
        return song
    }

    suspend fun removeFromPlayListById(id: Int) {
        musicDb.getPlayListDao().deletePlayListById(id)
    }

    suspend fun updatePlayListById(id:Int,playList: PlayListModel){
       val playListData = NetworkHelper.convertObjectToString(playList)
        playListData?.let {
            musicDb.getPlayListDao().updatePlayList(id,it)
        }
    }
}