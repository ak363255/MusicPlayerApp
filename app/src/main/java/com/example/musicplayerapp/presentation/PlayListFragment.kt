package com.example.musicplayerapp.presentation

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.CreatePlaylistLayoutBinding
import com.example.musicplayerapp.databinding.PlayListFragmentBinding
import com.example.recipecore.domain.model.PlayListModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlayListFragment : Fragment(R.layout.play_list_fragment) {

    private val playListViewModel: PlayListViewModel by viewModels {
        PlayListViewModel.PlayListViewModelFactory()
    }
    private var playListAdapter: MusicPlayListAdapter? = null
    private var playListFragmentBinding: PlayListFragmentBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playListViewModel.getPlayList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playListFragmentBinding = PlayListFragmentBinding.bind(view)
        initUi()
        observeChanges()
    }

    private fun observeChanges() {
        playListViewModel.playList.observe(viewLifecycleOwner) {
            updatePlayList(it)
        }
    }

    private fun setFragmentListener() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            PLAYLIST_UPDATED,
            viewLifecycleOwner
        ) { requestKey, bundle ->
            if (requestKey == PLAYLIST_UPDATED) {
                if (bundle.containsKey(PlayListDetailFragment.PLAYLIST_DATA)) {
                    val playlist =
                        bundle.getParcelable(PlayListDetailFragment.PLAYLIST_DATA) as PlayListModel?
                    updatePlayListAt(playlist)
                }
            }

        }
    }

    private fun updatePlayListAt(playlist: PlayListModel?) {
        var position = -1
        playListAdapter?.getList()?.forEachIndexed { index, data ->
            if (data.id == playlist?.id) {
                position = index
            }
        }
        if (position > 0) {
            val list = playListAdapter?.getList()
            list?.let {
                playlist?.let {
                    list[position] = playlist
                    playListAdapter?.notifyItemChanged(position,list[position])
                }
            }
        }
    }

    private fun initUi() {
        setPlayListRv()
        setClickListeners()
        setFragmentListener()
    }

    private fun setPlayListRv() {
        playListAdapter = MusicPlayListAdapter {
            val bundle = Bundle()
            bundle.putParcelable(PlayListDetailFragment.PLAYLIST_DATA, it)
            (requireActivity() as PlayListActivity).addFragmentWithBackStack(
                PlayListDetailFragment.getInstance(
                    bundle
                )
            )
        }
        playListAdapter?.setDelteCallback {
            deletePlayList(it)
        }
        playListFragmentBinding?.playlistRv?.apply {
            adapter = playListAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL, false
            )
        }
    }

    private fun setClickListeners() {
        playListFragmentBinding?.backBtn?.setOnClickListener {
        }

        playListFragmentBinding?.addFloatingBtn?.setOnClickListener {
            openCreatePlayListDialog()
        }
    }

    private fun deletePlayList(it: PlayListModel) {
        playListViewModel.removePlayList(it.id ?: 0)
    }

    private fun openCreatePlayListDialog() {
        val layout = LayoutInflater.from(requireContext())
            .inflate(R.layout.create_playlist_layout, null, false)
        val binder = CreatePlaylistLayoutBinding.bind(layout)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(layout)
            .setMessage("PlayList Details")
            .setPositiveButton("ADD") { dialog, _ ->
                val playListName = binder.playlistEditText.text.toString()
                val createdBy = binder.createdByEdittext.text.toString()
                if (TextUtils.isEmpty(playListName) || TextUtils.isEmpty(createdBy)) {
                    Toast.makeText(
                        requireContext(),
                        "PlayList Name Or Created By Cannot be Blank",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    createPlayList(playListName, createdBy)
                    dialog?.dismiss()
                }
            }.create().show()
    }

    private fun createPlayList(playListName: String, createdBy: String) {
        val playList = PlayListModel().apply {
            this.playListName = playListName
            this.createdBy = createdBy
            this.playList = mutableListOf()
        }
        playListViewModel.addPlayList(playList)
    }

    private fun updatePlayList(playList: List<PlayListModel>) {
        playListAdapter?.updateList(playList.toMutableList())
    }

    companion object {
        fun getInstance(): PlayListFragment {
            return PlayListFragment()
        }
        const val PLAYLIST_UPDATED = "playlist_updatad"
    }
}