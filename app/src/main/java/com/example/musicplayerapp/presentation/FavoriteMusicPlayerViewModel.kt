package com.example.musicplayerapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dbmodule.MusicDbHelper
import com.example.musicplayerapp.domain.model.Song
import kotlinx.coroutines.launch

class FavoriteMusicPlayerViewModel :ViewModel(){

    private val _favoriteMusicList :MutableLiveData<List<Song>> = MutableLiveData()
     val favoriteMusicList :MutableLiveData<List<Song>> get() = _favoriteMusicList

    fun getFavoriteMusicList() = viewModelScope.launch {
        val songs = MusicDbHelper.getAllSongs()
        _favoriteMusicList.postValue(songs)
    }


    class FavoriteMusicPlayerViewModelFactory():  ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FavoriteMusicPlayerViewModel() as T
        }
    }
}