package com.example.musicplayerapp.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.ActivityMainBinding
import com.example.musicplayerapp.domain.Utility
import com.example.musicplayerapp.domain.model.Song
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MainActivity :  BasePlayerActivity() {
    lateinit var  binding:ActivityMainBinding
    private var musicAdapter:MusicAdapter? = null
    private   val READ_EXTERNAL_STORAGE_ID = 100
    private var readPermissionGranted:Boolean = false
    private var writePermissionGranted:Boolean = false
    private lateinit var permissionLauncher:ActivityResultLauncher<Array<String>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        setContentView(binding.root)
        hasReadPermission()
       // setDrawer()
        initUi()

    }

    private fun hasReadPermission() {
          permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){permissions ->
              readPermissionGranted = Utility.sdk29AndUp {
                  permissions[Manifest.permission.READ_MEDIA_AUDIO]?:false
              }?:permissions[Manifest.permission.READ_EXTERNAL_STORAGE]?:false
              if(readPermissionGranted){
                  loadAudioFromExternalStorage()
              }else{
                  Toast.makeText(this,"Read Permission Required",Toast.LENGTH_LONG).show()
              }

          }
          updateOrRequestPermissions()

    }

    private fun updateOrRequestPermissions() {
       readPermissionGranted =
           Utility.sdk29AndUp {
               ContextCompat.checkSelfPermission(
                   this,
                   Manifest.permission.READ_MEDIA_AUDIO
               ) == PackageManager.PERMISSION_GRANTED
           }?:ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val permissionsToRequest = mutableListOf<String>()

        if(!readPermissionGranted) {
            permissionsToRequest.add(
                Utility.sdk29AndUp {
               Manifest.permission.READ_MEDIA_AUDIO
            }?:Manifest.permission.READ_EXTERNAL_STORAGE
            )

        }
        if(permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }else{
            loadAudioFromExternalStorage()
        }
    }


    private  fun loadAudioFromExternalStorage() {
        songPlayerViewModel.loadSongsFromExternalStorageStorage(contentResolver)
    }

    private fun initUi() {
        setClickListeners()
        setMusicRecyclerView()
        observeChanges()
    }

    private fun observeChanges() {
        songPlayerViewModel.songList.observe(this){
            initLocalUi(it)
            if(it.size>0){
                musicAdapter?.updateList(it.toMutableList())
            }
        }
    }

    private fun initLocalUi(songs: List<Song>) {
        binding.totalSongsTv.text = "Total Songs (${songs.size})"
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()

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

    private fun openPlayerActivity(song: Song) {
            songPlayerViewModel.songList.value?.let {
                val songList = ArrayList<Song>()
                it.forEach {
                    songList.add(it)
                }
                PlayerActivity.start(this,song,songList)
            }
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
     /*   binding.navView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.exit ->{
                    showExitDialog()
                    return@setNavigationItemSelectedListener  true
                }
            }
            false
        }*/
    }


    private fun showExitDialog() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Exit")
             .setMessage("Do You want to Close App ?")
               .setPositiveButton("Yes"){_,_ ->
                  finishAffinity()
               }
            .setNegativeButton("No"){dialog,_ ->
                 dialog?.dismiss()
            }.create().show()
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
        val songList = songPlayerViewModel.songList.value
        songList?.let {
            if(it.size > 0){
                it.toMutableList().shuffle()
                openPlayerActivity(it[0])
            }
        }
    }
}