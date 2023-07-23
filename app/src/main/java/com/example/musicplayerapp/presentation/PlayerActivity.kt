package com.example.musicplayerapp.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicplayerapp.R
import com.example.musicplayerapp.domain.Utility
import com.example.musicplayerapp.domain.Utility.orFalse
import com.example.musicplayerapp.domain.model.Song
import com.example.musicplayerapp.domain.model.Song.CREATOR.createMediaItem
import com.example.musicplayerapp.domain.model.Song.CREATOR.toSong
import com.google.gson.Gson

class PlayerActivity : BasePlayerActivity() {

    private var mSong: Song? = null
    private var mSongList: MutableList<Song>? = null
    private lateinit var binding:com.example.musicplayerapp.databinding.ActivityPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_player)
        setContentView(binding.root)
        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.extras?.apply {
            if (containsKey(PLAY_LIST_KEY)) {
                mSongList = getParcelableArrayList(PLAY_LIST_KEY)
            }

            if (containsKey(Song::class.java.name)) {
                mSong = getParcelable<Song>(Song::class.java.name) as Song
            }

            if (containsKey(MEDIA_ITEM_KEY)) {
                mSong = Gson().fromJson (getString(MEDIA_ITEM_KEY), Song::class.java)
            }
        }
        setData(mSong)
        observeChanges()
    }

    private fun observeChanges() {

        with(songPlayerViewModel){
            songDurationData.observe(this@PlayerActivity){

            }
            songDurationTextData.observe(this@PlayerActivity){

            }
            songPositionData.observe(this@PlayerActivity){
                  setSeekbar(it)
            }
            isRepeatData.observe(this@PlayerActivity){
                chageRepeatIcon(it)

            }
            isShuffleData.observe(this@PlayerActivity){

            }
            isPlayingData.observe(this@PlayerActivity){
                setPlayPauseBtn(it)
            }

            isFavoriteSong.observe(this@PlayerActivity){
                setFavoriteSongUi(it)
            }

            mediaItemData.observe(this@PlayerActivity){
                updateUiAccordingToMediaItem(it)
            }
        }
    }

    private fun setFavoriteSongUi(it: Boolean?) {
        if(it == true){
            binding.favBtn.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.favorite_icon))
        }else{
            binding.favBtn.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.fav_empty))
        }
    }

    private fun chageRepeatIcon(it: Boolean?) {
        if(it==true){
            binding.repeatBtn.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.repeat_one))
        }else{
            binding.repeatBtn.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.repeat))
        }
    }

    private fun updateUiAccordingToMediaItem(mediaItem:MediaItem?) {
        val song = mediaItem?.toSong()
        song?.let {
            setUi(it)
        }
    }

    private fun setSeekbar(it: Long?) {
        it?.let {
               binding.seekBar.progress = it.toInt()
               binding.startTv.text = Utility.formatTimeInMillisToString(it)
        }
    }

    private fun setPlayPauseBtn(isPlaying: Boolean?) {
        if(isPlaying==true){
            binding.playPauseBtn.icon = ContextCompat.getDrawable(this,R.drawable.pause)
        }else{
            binding.playPauseBtn.icon = ContextCompat.getDrawable(this,R.drawable.play_icon)
        }
    }

    private fun setUi(mSong: Song?) {
        mSong?.let { song ->
            song.albumArt?.let {path ->
                binding.songThumbnail.setImageURI(path.toUri())
            }
            binding.songTitle.text = song.songName
            binding.songNameTv.text = song.albumName
            binding.playPauseBtn.setOnClickListener {
                toggle()
            }
            binding.nextBtn.setOnClickListener {
                next()
            }
            binding.prevBtn.setOnClickListener {
                previous()
            }
            binding.repeatBtn.setOnClickListener {
                onRepeatButtonClicked(it)
            }
            binding.backBtn.setOnClickListener {
                finish()
            }
            binding.shareBtn.setOnClickListener {
                onShareButtonClicked()
            }
            binding.favBtn.setOnClickListener {
                onFavoriteBtnClicked()
            }
            song.duration?.let {
                binding.seekBar.max = it
                binding.endTv.text = Utility.formatTimeInMillisToString(it.toLong())
            }
            binding.seekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if(p2){
                        p0?.let {
                           it.progress?.let {
                               seekTo(it.toLong())
                           }
                        }
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }

            })

        }

        isFavoriteSong(mSong?.id)
    }

    private fun onFavoriteBtnClicked() {
        val isFavoriteSong = songPlayerViewModel.isFavoriteSong.value.orFalse()
        if(isFavoriteSong){
            songPlayerViewModel.removeFavoriteSong(mSong?.id)
        }else{
            songPlayerViewModel.addFavoriteSong(mSong)
        }
    }

    private fun onShareButtonClicked() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "audio/*"
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(mSong?.path))
        startActivity(Intent.createChooser(intent,"Share Music"))
    }

    private fun onRepeatButtonClicked(it: View?) {
           repeat()
    }

    fun setData(song : Song?){
        song?.let {song->
            val mediaItems = ArrayList<MediaItem>()
            mSongList?.forEach {
                mediaItems.add(it.createMediaItem())
            }
            play(mediaItems, song.createMediaItem())
        }?:{
            val mediaItems = ArrayList<MediaItem>()
            mSongList?.forEach {
                mediaItems.add(it.createMediaItem())
            }
            if(mediaItems.size != 0){
                play(mediaItems, mediaItems.get(0))
            }
        }
    }

    fun setPlayList(mediaItems:MutableList<MediaItem>,player:ExoPlayer){
        mediaItems.forEach {
            player.addMediaItem(it)
        }
    }

    companion object{
        fun start(context: Context, song: Song, songList: ArrayList<Song>) {
            val intent = Intent(context, PlayerActivity::class.java).apply {
                putExtra(Song::class.java.name, song)
                putExtra(PLAY_LIST_KEY, songList)
            }
            context.startActivity(intent)
        }
    }
}