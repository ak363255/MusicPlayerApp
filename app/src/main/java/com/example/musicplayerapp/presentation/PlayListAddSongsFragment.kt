package com.example.musicplayerapp.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.PlaylistAddSongsFragmentBinding
import com.example.musicplayerapp.domain.model.Song
import com.example.recipecore.domain.model.PlayListModel
import com.example.recipecore.domain.utils.Utility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayListAddSongsFragment : Fragment(R.layout.playlist_add_songs_fragment), TextWatcher {

    private var playlistAddSongsFragmentBinding: PlaylistAddSongsFragmentBinding? = null
    private var debounce: ((String) -> Unit)? = null
    private val WAIT_MS = 100L
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var playListUpdate = false
    private var filterSongsScope = CoroutineScope(Dispatchers.IO + Job())
    private var refreshRvScope = CoroutineScope(Dispatchers.IO + Job())
    private val playListAddSongViewModel:PlayListAddSongViewModel by viewModels{
        PlayListAddSongViewModel.PlayListAddSongViewModelFactory()
    }

    private var musicAdapter: AddSongToPlayListAdapter? = null
    private val songList: List<Song> by lazy {
        (requireActivity() as PlayListActivity).songPlayerViewModel.songList.value ?: listOf()
    }

    private var playListModel:PlayListModel? = null

    private fun refreshRv(){
        refreshRvScope.cancel()
        refreshRvScope = CoroutineScope(Dispatchers.IO + Job())
        refreshRvScope.launch {
            delay(2000)
            CoroutineScope(Dispatchers.Main).launch {
                 musicAdapter?.updateList(songList.toMutableList())
            }
        }

    }

    private fun addDebounceFunctionlity() {
        refreshRvScope.cancel()
        debounce = Utility.debounce(WAIT_MS, scope) { params ->
            filterSongs(params)
        }
    }

    private fun filterSongs(params: String) {
        filterSongsScope.cancel()
        filterSongsScope = CoroutineScope(Dispatchers.IO + Job())
        filterSongsScope.launch {
            val filteredSongList = mutableListOf<Song>()
            songList.forEach {
                if (it.songName?.lowercase()?.contains(params.lowercase()) ?: false || it.albumName?.lowercase()?.contains(params.lowercase()) ?: false) {
                    filteredSongList.add(it)
                }
            }
            if (filteredSongList.size > 0) {
                CoroutineScope(Dispatchers.Main).launch {
                    musicAdapter?.updateList(filteredSongList)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readArguments()

    }

    private fun readArguments() {
         arguments?.let {
             if(it.containsKey(PlayListDetailFragment.PLAYLIST_DATA)){
                 playListModel = it.getParcelable(PlayListDetailFragment.PLAYLIST_DATA) as PlayListModel?
             }
         }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistAddSongsFragmentBinding = PlaylistAddSongsFragmentBinding.bind(view)
        initUi()
    }

    private fun initUi() {
        setMusicRecyclerView()
        addDebounceFunctionlity()
        playlistAddSongsFragmentBinding?.searchEditTv?.addTextChangedListener(this)
        observeChanges()
    }

    private fun observeChanges() {
        playListAddSongViewModel.playlistUpdated.observe(viewLifecycleOwner){
            if(it){
                playListUpdate = true
                playlistAddSongsFragmentBinding?.root?.let {
                    (requireActivity() as PlayListActivity).showSnackBar(it,"Song added to playlist",R.color.white,"VIEW"){
                         requireActivity().onBackPressed()
                    }
                }
            }else{
                playlistAddSongsFragmentBinding?.root?.let {
                    (requireActivity() as PlayListActivity).showSnackBar(it,"This song already exists",R.color.white,"OK"){

                    }
                }
            }
        }
    }

    private fun setMusicRecyclerView() {
        musicAdapter = AddSongToPlayListAdapter {
            addToThisPlayList(it)
        }
        playlistAddSongsFragmentBinding?.songRv?.apply {
            adapter = musicAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL, false
            )
        }
        musicAdapter?.updateList(songList.toMutableList())
    }

    private fun addToThisPlayList(song: Song) {
        playListAddSongViewModel.updatePlaylist(playListModel,song)
    }

    companion object {
        fun getInstance(bundle: Bundle): PlayListAddSongsFragment {
            return PlayListAddSongsFragment().apply {
                arguments = bundle
            }
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (isValidKeyWord(p0?.toString() ?: "")) {
            debounce?.invoke(p0?.toString() ?: "")
        } else {
             if(p0?.length ==0){
                 refreshRv()
             }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(playListUpdate){
            setFragmentResult(PlayListDetailFragment.PLAYLIST_DATA_UPDATED,Bundle().also {
                    it.putParcelable(PlayListDetailFragment.PLAYLIST_DATA,playListModel)
            })
        }
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    private fun isValidKeyWord(keyWord: String?): Boolean {
        return (keyWord?.length ?: 0) >= 3
    }
}


