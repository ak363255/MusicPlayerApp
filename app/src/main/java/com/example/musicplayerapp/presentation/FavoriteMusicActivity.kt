package com.example.musicplayerapp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.ActivityFavoriteMusicBinding
import com.example.musicplayerapp.domain.model.Song

class FavoriteMusicActivity : BasePlayerActivity() {
    lateinit var binding:ActivityFavoriteMusicBinding
    private var songList:MutableList<Song> = mutableListOf()
    private var favoriteMusicAdapter:FavoriteMusicAdapter? = null
    private val favoriteMusicPlayerViewModel: FavoriteMusicPlayerViewModel by lazy {
        ViewModelProvider(
            this, FavoriteMusicPlayerViewModel.FavoriteMusicPlayerViewModelFactory(

            )
        )[FavoriteMusicPlayerViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_favorite_music)
        setContentView(binding.root)
        favoriteMusicPlayerViewModel.getFavoriteMusicList()
        initUi()
    }
    private fun initUi(){
        hideToolbar()
        setFavoriteMusicRecyclerView()
        setClickListeners()
        observeChanges()
    }

    private fun observeChanges() {
        favoriteMusicPlayerViewModel.favoriteMusicList.observe(this){
            songList = it.toMutableList()
            updateRecyclerView(songList)
        }
    }

    private fun updateRecyclerView(songList: MutableList<Song>) {
        favoriteMusicAdapter?.updateList(songList)
    }

    private fun openPlayerActivity(song: Song) {
        songList.let {
            val songList = ArrayList<Song>()
            it.forEach {
                songList.add(it)
            }
            PlayerActivity.start(this,song,songList)
        }
    }

    private fun setFavoriteMusicRecyclerView() {
        favoriteMusicAdapter = FavoriteMusicAdapter{
            openPlayerActivity(it)
        }
        binding.gridRv.apply {
            adapter = favoriteMusicAdapter
          //  orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 3
            setPageTransformer(SliderTransformer(3))
        }
    }

    private fun setClickListeners() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}