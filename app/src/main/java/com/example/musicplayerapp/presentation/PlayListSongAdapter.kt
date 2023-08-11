package com.example.musicplayerapp.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.MusicItemViewBinding
import com.example.musicplayerapp.databinding.PlaylistMusicItemViewBinding
import com.example.musicplayerapp.domain.Utility
import com.example.musicplayerapp.domain.model.Song

class PlayListSongAdapter(private val callback:ItemClickedCallback):RecyclerView.Adapter<PlayListSongAdapter.MusicViewHolder>() {

    private var data:MutableList<Song> = mutableListOf()
    inner class MusicViewHolder(val binding:PlaylistMusicItemViewBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlayListSongAdapter.MusicViewHolder {
        return MusicViewHolder(PlaylistMusicItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: PlayListSongAdapter.MusicViewHolder, position: Int) {
          initUi(holder.binding,position)
    }

    private fun initUi(binding: PlaylistMusicItemViewBinding, position: Int) {
        val data = data[position]
        binding.songTitle.text = data.songName
        binding.albumName.text = data.albumName
        binding.thumbnailSong.setImageURI(data.albumArt?.toUri())
        binding.menu.setOnClickListener {
            callback.menuClicked(data)
        }
        binding.root.setOnClickListener {
            callback.itemClicked(data)
        }
        binding.songTitle.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
        binding.albumName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.ads_5C5F61))

    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateList(list:MutableList<Song>){
        this.data = list
        notifyDataSetChanged()
    }

    interface ItemClickedCallback{
        fun menuClicked(song:Song)
        fun itemClicked(song: Song)
    }
}