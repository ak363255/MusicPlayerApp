package com.example.musicplayerapp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.ActivityPlayListBinding

class PlayListActivity : AppCompatActivity() {
    lateinit var binding:ActivityPlayListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_play_list)
        setContentView(binding.root)
    }
}