package com.example.musicplayerapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dbmodule.MusicDbHelper
import com.example.dbmodule.PlayListDbHelper
import com.example.musicplayerapp.domain.model.Song
import com.example.recipecore.domain.model.PlayListModel
import kotlinx.coroutines.launch

class PlayListViewModel :ViewModel(){

    private val _playList :MutableLiveData<List<PlayListModel>> = MutableLiveData()
     val playList :MutableLiveData<List<PlayListModel>> get() = _playList

    fun getPlayList() = viewModelScope.launch {
        val playList = PlayListDbHelper.getAllPlayList()
        _playList.postValue(playList)
    }

    fun addPlayList(playList:PlayListModel) = viewModelScope.launch{
        PlayListDbHelper.insertPlayListData(playList)
        getPlayList()
    }
    fun removePlayList(id:Int) = viewModelScope.launch {
        PlayListDbHelper.removeFromPlayListById(id)
        getPlayList()
    }


    class PlayListViewModelFactory():  ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlayListViewModel() as T
        }
    }
}