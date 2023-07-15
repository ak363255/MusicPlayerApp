package com.example.musicplayerapp.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.databinding.MusicItemViewBinding
import com.example.musicplayerapp.domain.model.Song

class MusicAdapter(private val callback:(Song)->Unit):RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    private var data:MutableList<Song> = mutableListOf()
    inner class MusicViewHolder(val binding:MusicItemViewBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MusicAdapter.MusicViewHolder {
        return MusicViewHolder(MusicItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MusicAdapter.MusicViewHolder, position: Int) {
          initUi(holder.binding,position)
    }

    private fun initUi(binding: MusicItemViewBinding, position: Int) {
        val data = data[position]
        binding.songTitle.text = data.songName
        binding.albumName.text = data.albumName
        binding.duration.text = data.duration.toString()
        binding.root.setOnClickListener {
            callback.invoke(data)
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