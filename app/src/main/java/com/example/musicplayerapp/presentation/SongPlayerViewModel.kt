package com.example.musicplayerapp.presentation

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.example.dbmodule.MusicDbHelper
import com.example.musicplayerapp.domain.Utility
import com.example.musicplayerapp.domain.Utility.formatTimeInMillisToString
import com.example.musicplayerapp.domain.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SongPlayerViewModel : ViewModel() {

    private val _mediaItemData = MutableLiveData<MediaItem?>()
    val mediaItemData: LiveData<MediaItem?> = _mediaItemData
    private val _isPlayingData = MutableLiveData<Boolean>()
    val isPlayingData: LiveData<Boolean> = _isPlayingData
    private val _songDurationTextData = MutableLiveData<String>()
    val songDurationTextData: LiveData<String> = _songDurationTextData
    private val _songDurationData = MutableLiveData<Int>()
    val songDurationData: LiveData<Int> = _songDurationData
    private val _songPositionTextData = MutableLiveData<String>()
    val songPositionTextData: LiveData<String> = _songPositionTextData
    private val _songPositionData = MutableLiveData<Long>()
    val songPositionData: LiveData<Long> = _songPositionData
    private val _isShuffleData = MutableLiveData<Boolean>()
    val isShuffleData: LiveData<Boolean> = _isShuffleData
    private val _isRepeatAllData = MutableLiveData<Boolean>()
    val isRepeatAllData: LiveData<Boolean> = _isRepeatAllData
    private val _isRepeatData = MutableLiveData<Boolean>()
    val isRepeatData: LiveData<Boolean> = _isRepeatData

    private val _isFavoriteSong: MutableLiveData<Boolean> = MutableLiveData()
    val isFavoriteSong: LiveData<Boolean> get() = _isFavoriteSong


    private val _favoriteSongRemoved: MutableLiveData<Boolean> = MutableLiveData()
    val favoriteSongRemoved: MutableLiveData<Boolean> get() = _favoriteSongRemoved

    private val _favoriteSongAdded: MutableLiveData<Boolean> = MutableLiveData()
    val favoriteSongAdded: MutableLiveData<Boolean> get() = _favoriteSongAdded


    private val _currentMediaIndex: MutableLiveData<Int> = MutableLiveData()
    val currentMediaIndex: LiveData<Int> get() = _currentMediaIndex

    private val _songList:MutableLiveData<List<Song>> = MutableLiveData()
     val songList: LiveData<List<Song>> get() = _songList

    fun updateMediaItem(mediaItem: MediaItem?) {
        _mediaItemData.value = mediaItem
    }

    fun shuffle() {
        _isShuffleData.value = _isShuffleData.value != true
    }

    fun isFavoriteSong(id: String?) = viewModelScope.launch {
        id?.let {
            val song = MusicDbHelper.getMusicById(id)
            if (song === null) {
                _isFavoriteSong.postValue(false)
            } else {
                _isFavoriteSong.postValue(true)
            }
        }
    }

    fun removeFavoriteSong(id: String?) = viewModelScope.launch {
        if (id == null) {
            _favoriteSongRemoved.postValue(false)
        } else {
            id.let {
                MusicDbHelper.removeFromFavMusiceById(it)
                isFavoriteSong(it)
                _favoriteSongRemoved.postValue(true)
            }
        }
    }

    fun addFavoriteSong(song: Song?) = viewModelScope.launch {
        if (song == null || song.id == null) {
            _favoriteSongAdded.postValue(false)
        } else {
            song.let {
                MusicDbHelper.insertMusicData(it)
                isFavoriteSong(it.id)
                _favoriteSongAdded.postValue(true)
            }
        }
    }

    fun repeatAll() {
        _isRepeatAllData.value = _isRepeatAllData.value != true
    }

    fun repeat() {
        _isRepeatData.value = _isRepeatData.value != true
    }

    fun updateCurrentMediaIndex(pos: Int) {
        _currentMediaIndex.postValue(pos)
    }

    fun setPlayingStatus(playStatus: Boolean) {
        _isPlayingData.value = playStatus
    }

    fun stop() {
        _songPositionData.value = 0
        _songPositionTextData.value =
            formatTimeInMillisToString(_songPositionData.value?.toLong() ?: 0)
    }

    fun setChangePosition(currentPosition: Long, duration: Long) {
        _songPositionTextData.value = formatTimeInMillisToString(currentPosition)
        _songPositionData.value = currentPosition

        if (_songDurationTextData.value != formatTimeInMillisToString(duration)) {
            _songDurationTextData.value = formatTimeInMillisToString(duration)
            _songDurationData.value = duration.toInt()
        }
    }

    fun loadSongsFromExternalStorageStorage(contentResolver: ContentResolver) =
        viewModelScope.launch {
            val collection = Utility.sdk29AndUp {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } ?: MediaStore.Audio.Media.EXTERNAL_CONTENT_URI


            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Albums.ALBUM_ID,
                MediaStore.Audio.Media.DATA
            )
            contentResolver.query(collection, projection, null, null, null, null)?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val dispalyName = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val duration = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val album = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val albumId = it.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ID)
                val dataCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val songs = mutableListOf<Song>()
                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val songName = it.getString(dispalyName)
                    val duration = it.getInt(duration)
                    val albumName = it.getString(album)
                    val songCover: Uri = Uri.parse("content://media/external/audio/albumart")
                    val albumArtUri = ContentUris.withAppendedId(songCover, it.getLong(albumId))
                    val path =
                        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                    val dataPath = it.getString(dataCol)
                    songs.add(
                        Song(
                            id = id.toString(),
                            songName = songName,
                            albumName = albumName,
                            duration = duration,
                            path = dataPath.toString(),
                            albumArt = albumArtUri.toString()
                        )
                    )

                }
                _songList.postValue(songs)
            }


        }

    companion object {
        private var mInstance: SongPlayerViewModel? = null

        @Synchronized
        fun getPlayerViewModelInstance(): SongPlayerViewModel {
            if (mInstance == null) {
                mInstance = SongPlayerViewModel()
            }
            return mInstance as SongPlayerViewModel
        }
    }
}