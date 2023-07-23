package com.example.musicplayerapp.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.databinding.PlaylistItemLayoutBinding
import com.example.recipecore.domain.model.PlayListModel

class MusicPlayListAdapter (private val callback:(PlayListModel)->Unit): RecyclerView.Adapter<MusicPlayListAdapter.MusicPlayListViewHolder>() {

    private var data:MutableList<PlayListModel> = mutableListOf()
    inner class MusicPlayListViewHolder(val binding: PlaylistItemLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MusicPlayListAdapter.MusicPlayListViewHolder {
        return MusicPlayListViewHolder(PlaylistItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MusicPlayListViewHolder, position: Int) {
        initUi(holder.binding,position)
    }

    private fun initUi(binding: PlaylistItemLayoutBinding, position: Int) {
        val data = data[position]
        val size = data.playList?.size?:0
        binding.playlistName.text = data.playListName?:""
        binding.createdby.text = data.createdBy?:""
        binding.totalSongs.text = "$size songs"
        binding.deleteIcon.setOnClickListener {
            deleteCallback?.invoke(data)
        }
        binding.root.setOnClickListener {
            callback(data)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateList(list:MutableList<PlayListModel>){
        this.data = list
        notifyDataSetChanged()
    }

    private var deleteCallback:((PlayListModel)->Unit)? = null
    fun setDelteCallback(deleteCallback:(PlayListModel)->Unit){
        this.deleteCallback = deleteCallback
    }
    fun getList() = data
}