package com.example.musicplayerapp.presentation

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.ActivityMainBinding
import com.example.musicplayerapp.domain.Utility
import com.example.musicplayerapp.domain.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var  binding:ActivityMainBinding
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private var musicAdapter:MusicAdapter? = null
    private   val READ_EXTERNAL_STORAGE_ID = 100
    private var readPermissionGranted:Boolean = false
    private var writePermissionGranted:Boolean = false
    private lateinit var permissionLauncher:ActivityResultLauncher<Array<String>>
    private val songListUpdatedLiveData:MutableLiveData<MutableList<Song>> = MutableLiveData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        setContentView(binding.root)
        hasReadPermission()
        setDrawer()
        initUi()

    }

    private fun hasReadPermission() {
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){permissions ->
            readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE]?:readPermissionGranted
            writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE]?:writePermissionGranted
            if(readPermissionGranted){
                loadAudioFromExternalStorage()
            }else{
                Toast.makeText(this,"Read Permission Required",Toast.LENGTH_LONG).show()
            }

        }
        updateOrRequestPermissions()
    }

    private fun updateOrRequestPermissions() {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission || minSdk29

        val permissionsToRequest = mutableListOf<String>()
        if(!writePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if(!readPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if(permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }else{
            loadAudioFromExternalStorage()
        }
    }


    private  fun loadAudioFromExternalStorage() {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            val collection = Utility.sdk29AndUp {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            }?: MediaStore.Audio.Media.EXTERNAL_CONTENT_URI


            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID
            )

            contentResolver?.query(collection,projection,null,null,null,null)?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val dispalyName = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val duration = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val album = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val albumId = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val songs = mutableListOf<Song>()
                while(it.moveToNext()){
                    val id = it.getLong(idColumn)
                    val songName = it.getString(dispalyName)
                    val duration = it.getInt(duration)
                    val albumName = it.getString(album)
                    val songCover: Uri = Uri.parse("content://media/external/audio/albumart")
                    val albumArtUri = ContentUris.withAppendedId(songCover,it.getLong(albumId))
                    val path = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id)
                    songs.add(Song(
                        id = id,
                        songName = songName,
                        albumName = albumName,
                        duration = duration,
                        path = path.toString(),
                        albumArt = albumArtUri.toString()
                    ))

                }
                songListUpdatedLiveData.postValue(songs)
            }
        }
    }

    private fun setDrawer() {
        setSupportActionBar(binding.toolBar)
        actionBarDrawerToggle =object: ActionBarDrawerToggle(this,binding.drawerLayout,R.string.open,R.string.close){
            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerOpened(drawerView: View) {
            }
        }
        actionBarDrawerToggle.drawerArrowDrawable.color = ContextCompat.getColor(this,R.color.white)
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initUi() {
        setClickListeners()
        setMusicRecyclerView()
        observeChanges()
    }

    private fun observeChanges() {
        songListUpdatedLiveData.observe(this){
            if(it.size>0){
                musicAdapter?.updateList(it)
            }
        }
    }

    private fun setMusicRecyclerView() {
        musicAdapter = MusicAdapter{
            openPlayerActivity(it)
        }
        binding.songRv.apply {
            adapter = musicAdapter
            layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.VERTICAL,false)
        }
    }

    private fun openPlayerActivity(it: Song) {
        val playerIntent = Intent(this,PlayerActivity::class.java)
        startActivity(playerIntent)
    }

    private fun getDummySongs():List<String>{
        val list = mutableListOf<String>()
        for(i in 0..10){
             list.add("$i")
        }
        return list
    }

    private fun setClickListeners() {
        binding.favoriteBtn.setOnClickListener {
            openFavoriteActivity()
        }
        binding.playlistBtn.setOnClickListener {
            openPlayListActivity()
        }
        binding.shuffleBtn.setOnClickListener {
            onShuffleBtnClicked()
        }
    }

    private fun openFavoriteActivity(){
        val favoriteIntent = Intent(this,FavoriteMusicActivity::class.java)
        startActivity(favoriteIntent)
    }

    private fun openPlayListActivity(){
        val playListIntent = Intent(this,PlayListActivity::class.java)
        startActivity(playListIntent)
    }
    private fun onShuffleBtnClicked(){

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}