package com.example.musicplayerapp.presentation

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.FavoriteMusicItemBinding
import com.example.musicplayerapp.domain.model.Song

class FavoriteMusicAdapter (private val callback:(Song)->Unit): RecyclerView.Adapter<FavoriteMusicAdapter.FavoriteMusicViewHolder>() {

    private var data:MutableList<Song> = mutableListOf()
    inner class FavoriteMusicViewHolder(val binding: FavoriteMusicItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteMusicAdapter.FavoriteMusicViewHolder {
        return FavoriteMusicViewHolder(FavoriteMusicItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: FavoriteMusicViewHolder, position: Int) {
        initUi(holder.binding,position)
    }

    private fun initUi(binding: FavoriteMusicItemBinding, position: Int) {
        val data = data[position]
        data.albumArt?.let {
            binding.songImageView.setImageURI(Uri.parse(it))
            binding.songImageView.background = ContextCompat.getDrawable(binding.root.context, R.drawable.gradient_shadow_for_card)
        }
        data.songName?.let {
            binding.songName.text = it
        }
        binding.root.setOnClickListener {
            callback(data)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateList(list:MutableList<Song>){
        this.data = list
        notifyDataSetChanged()
    }
}