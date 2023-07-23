package com.example.musicplayerapp.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dbmodule.PlayListDbHelper
import com.example.musicplayerapp.domain.model.Song
import com.example.recipecore.domain.model.PlayListModel
import kotlinx.coroutines.launch

class PlayListAddSongViewModel:ViewModel() {

    private val _playlistUpdated:MutableLiveData<Boolean> = MutableLiveData()
    val playlistUpdated: LiveData<Boolean> get() = _playlistUpdated

    fun updatePlaylist(playListModel: PlayListModel?,song:Song) = viewModelScope.launch {
        var alreaddyExist = false
        playListModel?.playList?.forEach {
            if(it.id == song.id){
                alreaddyExist = true
            }
        }
        if(alreaddyExist){
            _playlistUpdated.postValue(false)
        }else{
            playListModel?.playList?.let {list ->
                    val newList = list.toMutableList()
                    newList.add(song)
                   playListModel.playList =  newList
                    PlayListDbHelper.updatePlayListById(playListModel.id?:0,playListModel)
                    _playlistUpdated.postValue(true)

            }
        }
    }

    class PlayListAddSongViewModelFactory():ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlayListAddSongViewModel() as T
        }
    }
}