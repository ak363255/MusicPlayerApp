package com.example.musicplayerapp.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.MusicItemViewBinding
import com.example.musicplayerapp.domain.Utility
import com.example.musicplayerapp.domain.model.Song

class AddSongToPlayListAdapter(private val callback:(Song)->Unit):RecyclerView.Adapter<AddSongToPlayListAdapter.MusicViewHolder>() {

    private var data:MutableList<Song> = mutableListOf()
    inner class MusicViewHolder(val binding:MusicItemViewBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddSongToPlayListAdapter.MusicViewHolder {
        return MusicViewHolder(MusicItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: AddSongToPlayListAdapter.MusicViewHolder, position: Int) {
          initUi(holder.binding,position)
    }

    private fun initUi(binding: MusicItemViewBinding, position: Int) {
        val data = data[position]
        binding.songTitle.text = data.songName
        binding.albumName.text = data.albumName
        val duration = data.duration?:0
        binding.thumbnailSong.setImageURI(data.albumArt?.toUri())
        binding.duration.text = Utility.formatTimeInMillisToString(duration*1L)
        binding.root.setOnClickListener {
            callback.invoke(data)
        }
        binding.songTitle.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
        binding.albumName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.ads_5C5F61))
        binding.duration.setTextColor(ContextCompat.getColor(binding.root.context, R.color.ads_5C5F61))

    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateList(list:MutableList<Song>){
        this.data = list
        notifyDataSetChanged()
    }
}