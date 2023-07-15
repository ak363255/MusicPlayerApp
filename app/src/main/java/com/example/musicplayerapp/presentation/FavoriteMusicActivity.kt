package com.example.musicplayerapp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.ActivityFavoriteMusicBinding

class FavoriteMusicActivity : AppCompatActivity() {
    lateinit var binding:ActivityFavoriteMusicBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_favorite_music)
        setContentView(binding.root)
    }
}