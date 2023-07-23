package com.example.musicplayerapp.presentation

import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.PlayListDetailFragmentBinding
import com.example.recipecore.domain.model.PlayListModel
import com.example.recipecore.domain.utils.addClickSpan

class PlayListDetailFragment : Fragment(R.layout.play_list_detail_fragment) {

    private var playListDetailFragmentBinding: PlayListDetailFragmentBinding? = null
    private var playListModel: PlayListModel? = null
    private var playListDetailSongsAdapter: PlayListSongAdapter? = null
    private var updatePlayList = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readArguments()
    }

    private fun setFragmentListener() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            PLAYLIST_DATA_UPDATED,
            viewLifecycleOwner
        ) { requestKey, bundle ->
            if (requestKey == PLAYLIST_DATA_UPDATED) {
                if (bundle.containsKey(PLAYLIST_DATA)) {
                    updatePlayList = true
                    playListModel = bundle.getParcelable(PLAYLIST_DATA) as PlayListModel?
                    updatePlayList()
                }
            }

        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playListDetailFragmentBinding = PlayListDetailFragmentBinding.bind(view)
        initUi()
        setFragmentListener()
    }

    private fun updatePlayList() {
        setePlayListHeader()
        updatePlayListRv()
    }

    private fun setPlayListRv() {
        playListDetailSongsAdapter = PlayListSongAdapter {

        }
        playListDetailFragmentBinding?.playListRv?.apply {
            adapter = playListDetailSongsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        playListModel?.playList?.let {
            if (it.size > 0) {
                playListDetailSongsAdapter?.updateList(it.toMutableList())
            }
        }
    }

    private fun updatePlayListRv() {
        playListModel?.playList?.let {
            playListDetailSongsAdapter?.updateList(it.toMutableList())
        }
    }

    private fun initUi() {
        setePlayListHeader()
        setClickListener()
        setPlayListRv()
    }

    private fun setClickListener() {
        playListDetailFragmentBinding?.editImg?.setOnClickListener {

        }
        playListDetailFragmentBinding?.menuImg?.setOnClickListener {

        }

        playListDetailFragmentBinding?.addSongImg?.setOnClickListener {
            openPlayListAddSongFragment()
        }
        playListDetailFragmentBinding?.addSongTv?.setOnClickListener {
            openPlayListAddSongFragment()
        }
        playListDetailFragmentBinding?.addFloatingBtn?.setOnClickListener {
            openPlayListAddSongFragment()
        }
    }

    private fun openPlayListAddSongFragment() {
        val bundle = Bundle()
        bundle.putParcelable(PLAYLIST_DATA, playListModel)
        val openPlaylistAddSongFrag = PlayListAddSongsFragment.getInstance(bundle)
        (requireActivity() as PlayListActivity).addFragmentWithBackStack(openPlaylistAddSongFrag)
    }

    private fun setePlayListHeader() {
        val size = playListModel?.playList?.size ?: 0
        playListDetailFragmentBinding?.playListName?.text = playListModel?.playListName
        playListDetailFragmentBinding?.noOfSongTv?.text = "$size songs"
        val prefix = "by"
        val suffix = playListModel?.createdBy ?: ""
        val msg = SpannableString("$prefix $suffix")
        playListDetailFragmentBinding?.createdByTv?.let {
            msg.addClickSpan(
                prefix.length + 1,
                msg.length,
                R.color.white,
                true,
                requireContext(),
                it
            ) {}
            it.text = msg
        }

        if (size > 0) {
            playListDetailFragmentBinding?.shuffleParentView?.visibility = View.VISIBLE
            playListDetailFragmentBinding?.playParentView?.visibility = View.VISIBLE
            playListDetailFragmentBinding?.noSongsYetTv?.visibility = View.GONE
            if (TextUtils.isEmpty(playListModel?.playList?.get(0)?.albumArt)) {
                playListDetailFragmentBinding?.playlistDefaultImage?.visibility = View.VISIBLE
            } else {
                playListDetailFragmentBinding?.playlistDefaultImage?.visibility = View.GONE
                playListModel?.playList?.get(0)?.let { song ->
                    song.albumArt?.let {
                        playListDetailFragmentBinding?.playlistImage?.setImageURI(Uri.parse(it))
                    }
                }
            }


        } else {
            playListDetailFragmentBinding?.shuffleParentView?.visibility = View.GONE
            playListDetailFragmentBinding?.playParentView?.visibility = View.GONE
            playListDetailFragmentBinding?.noSongsYetTv?.visibility = View.VISIBLE
        }
    }

    private fun readArguments() {
        arguments?.let {
            if (it.containsKey(PLAYLIST_DATA)) {
                playListModel = it.getParcelable(PLAYLIST_DATA) as PlayListModel?
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (updatePlayList) {
            setFragmentResult(PlayListFragment.PLAYLIST_UPDATED, Bundle().also {
                it.putParcelable(PLAYLIST_DATA, playListModel)
            })
        }
    }

    companion object {
        fun getInstance(bundle: Bundle): PlayListDetailFragment {
            return PlayListDetailFragment().apply {
                this.arguments = bundle
            }
        }

        const val PLAYLIST_DATA = "playlist_data"
        const val PLAYLIST_DATA_UPDATED = "playlist_data_updated"
    }

}