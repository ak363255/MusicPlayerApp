package com.example.musicplayerapp.presentation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.musicplayerapp.R
import com.example.musicplayerapp.domain.model.Song

class PlayerActivity : BasePlayerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
    }

    fun start(context: Context, song: Song, songList: ArrayList<Song>) {
        val intent = Intent(context, PlayerActivity::class.java).apply {
            putExtra(Song::class.java.name, song)
            putExtra(PLAY_LIST_KEY, songList)
        }
        context.startActivity(intent)
    }
}