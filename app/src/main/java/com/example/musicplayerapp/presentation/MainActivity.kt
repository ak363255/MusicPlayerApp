package com.example.musicplayerapp.presentation

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class MainActivity : BasePlayerActivity() {
    lateinit var  binding:ActivityMainBinding
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
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
        setDrawer()
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
        songPlayerViewModel.songList.observe(this){
            if(it.size>0){
                musicAdapter?.updateList(it.toMutableList())
            }
        }
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
        binding.navView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.exit ->{
                    showExitDialog()
                    return@setNavigationItemSelectedListener  true
                }
            }
            false
        }
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
    /*

![addsongtoplaylist_ss](https://github.com/ak363255/MusicPlayerApp/assets/36182994/3da57d8c-570b-4c5a-9020-466602002dd6)
![createplaylist_ss](https://github.com/ak363255/MusicPlayerApp/assets/36182994/c1b255e3-ed90-4c7f-86be-674127a10403)
![favorite_screen_ss](https://github.com/ak363255/MusicPlayerApp/assets/36182994/209ab156-8084-4d86-bf7e-dc838fddd75a)
![favorite_ss_2](https://github.com/ak363255/MusicPlayerApp/assets/36182994/9b6b2145-a483-4d31-9cd4-3f5548cdedc6)
![home_ss](https://github.com/ak363255/MusicPlayerApp/assets/36182994/26ba3858-9d1e-4d88-b26e-bd95ae972248)
![menu_ss](https://github.com/ak363255/MusicPlayerApp/assets/36182994/468c593b-8f0b-4606-a1c5-91d19e704f2e)
![notification_ss](https://github.com/ak363255/MusicPlayerApp/assets/36182994/a5fb3a50-bfa7-4207-b02e-42b3c549bcd7)
![playing_ss](https://github.com/ak363255/MusicPlayerApp/assets/36182994/b2072241-a427-4325-aa5d-58d72af2bafb)
![playlist_detail_song_added_ss](https://github.com/ak363255/MusicPlayerApp/assets/36182994/94212e6b-278f-40d6-956c-ea2d7ec1644d)
![playlist_ss](https://github.com/ak363255/MusicPlayerApp/assets/36182994/720e75ec-a873-4e3e-8d1a-175909eb60a0)
![playlistdetails_ss](https://github.com/ak363255/MusicPlayerApp/assets/36182994/99667ce2-6785-4fea-989e-c3404851ec67)
![searchsong_ss](https://github.com/ak363255/MusicPlayerApp/assets/36182994/9a42ab68-62b4-488f-a88a-34b183884613)
     */
    private fun onShuffleBtnClicked(){
        val songList = songPlayerViewModel.songList.value
        songList?.let {
            if(it.size > 0){
                it.toMutableList().shuffle()
                openPlayerActivity(it[0])
            }
        }
    }

    val navigationItemClickListener =
        NavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.exit ->{
                    showExitDialog()
                    true
                }
                else -> {
                    false
                }
            }
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}