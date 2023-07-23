package com.example.musicplayerapp.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.Looper
import android.os.Message
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC
import androidx.media3.common.C.USAGE_MEDIA
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class ExoPlayerManager(
    val context:Context,
    private val callback: OnExoPlayerManagerCallback
) :Player.Listener{
    private val mAudioNoisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private var mWifiLock:WifiManager.WifiLock? = null
    private var mAudioManager:AudioManager? = null
    private var mPlayOnFocusGain:Boolean = false
    private var mAudioNoisyReceiverRegistered:Boolean = false
    private var mCurrentAudioFocusState = AUDIO_NO_FOCUS_CAN_DUCK
    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L
    private var player:ExoPlayer? = null
    private val mAudioNoisyReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent?.action){
                if(mPlayOnFocusGain || player != null && player?.playWhenReady == true){
                    val i = Intent(context,PlayerService::class.java).apply {
                        action = PlayerService.ACTION_CMD
                        putExtra(PlayerService.CMD_NAME,PlayerService.CMD_PAUSE)
                    }
                    //context?.applicationContext?.startService(i)
                }
            }
        }

    }
    private val mUpdateProgressHandler = object :android.os.Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            if(player?.isPlaying == true){
                val duration = player?.duration?:0
                val position = player?.currentPosition?:0
                callback.onUpdateProgress(duration,position)
            }
            sendEmptyMessageDelayed(0, UPDATE_PROGRESS_DELAY)
        }
    }

    fun setPlayList(mediaItems:MutableList<MediaItem>){
        mediaItems.forEach {
            player?.addMediaItem(it)
        }
    }

    private var mExoPlayerIsStopped = false
    private val mOnAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener {focusChange ->
       when(focusChange){
           AudioManager.AUDIOFOCUS_GAIN -> mCurrentAudioFocusState = AUDIO_FOCUSED
           AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->
               // Audio focus was lost, but it's possible to duck (i.e.: play quietly)
               mCurrentAudioFocusState = AUDIO_NO_FOCUS_CAN_DUCK
           AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
               // Lost audio focus, but will gain it back (shortly), so note whether
               // playback should resume
               mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK
               mPlayOnFocusGain = player != null && player?.playWhenReady ?: false
           }
           AudioManager.AUDIOFOCUS_LOSS ->
               // Lost audio focus, probably "permanently"
               mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK
       }
       configurePlayerState()

    }


    init {
        this.mAudioManager = context.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        this.mWifiLock = (context.applicationContext?.getSystemService(Context.WIFI_SERVICE)as WifiManager).createWifiLock(WifiManager.WIFI_MODE_FULL,"app_lock")
        initializePlayer()
    }

    private fun initializePlayer() {
        if(player==null){
            player = ExoPlayer.Builder(context.applicationContext).build()
            player?.addListener(this)
        }
    }


    private fun configurePlayerState() {
        if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_NO_DUCK) {
            // We don't have audio focus and can't duck, so we have to pause
            pause()
        } else {
            registerAudioNoisyReceiver()

            if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_CAN_DUCK)
            // We're permitted to play, but only if we 'duck', ie: play softly
                player?.volume = VOLUME_DUCK
            else
                player?.volume = VOLUME_NORMAL

            // If we were playing when we lost focus, we need to resume playing.
            if (mPlayOnFocusGain) {
                player?.playWhenReady = true
                mPlayOnFocusGain = false
            }
        }
    }

    private fun registerAudioNoisyReceiver() {
        if (!mAudioNoisyReceiverRegistered) {
            context.applicationContext.registerReceiver(
                mAudioNoisyReceiver,
                mAudioNoisyIntentFilter
            )
            mAudioNoisyReceiverRegistered = true
        }
    }
    fun pause() {
        player?.playWhenReady = false
        // While paused, retain the player instance, but give up audio focus.
        releaseResources(false)
        unregisterAudioNoisyReceiver()
    }

    fun stop(){
        giveUpAudioFocus()
        releaseResources(true)
        unregisterAudioNoisyReceiver()
    }

    fun seekTo(positoin:Long){
        registerAudioNoisyReceiver()
        player?.seekTo(positoin)
    }

    fun skipToPosition(position:Long){
        player?.let {
            if(position == 1L && it.hasNextMediaItem()){
                it.seekToNextMediaItem()
            }else if(position == -1L && it.hasPreviousMediaItem()){
                it.seekToPreviousMediaItem()
            }
        }
    }

    fun toggle(){
        if(player?.isPlaying == true)player?.pause() else player?.play()
    }
    fun getCurrentMediaItem():MediaItem?{
        return player?.currentMediaItem
    }


    fun hasNext():Boolean{
        return player?.hasNextMediaItem()?:false
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        callback.updateUiForPlayingMediaItem(mediaItem)
    }


    private fun unregisterAudioNoisyReceiver() {
        if (mAudioNoisyReceiverRegistered) {
            context.applicationContext.unregisterReceiver(mAudioNoisyReceiver)
            mAudioNoisyReceiverRegistered = false
        }
    }

    private fun releaseResources(releasePlayer: Boolean) {
        // Stops and releases player (if requested and available).
        if (releasePlayer) {
            player?.let { exoPlayer ->
                playbackPosition = exoPlayer.currentPosition
                currentItem = exoPlayer.currentMediaItemIndex
                playWhenReady = exoPlayer.playWhenReady
                exoPlayer.release()
                exoPlayer.removeListener(this)
            }
            mUpdateProgressHandler.removeMessages(0)
            mExoPlayerIsStopped = true
            mPlayOnFocusGain = false
            player = null
        }
        if (mWifiLock?.isHeld == true) {
            mWifiLock?.release()
        }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        player?.repeatMode = repeatMode
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        player?.shuffleModeEnabled = shuffleModeEnabled
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when(playbackState){
            Player.STATE_IDLE,Player.STATE_BUFFERING,Player.STATE_READY ->{
                mUpdateProgressHandler.sendEmptyMessage(0)
            }
            Player.STATE_ENDED ->{
                mUpdateProgressHandler.removeMessages(0)
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        callback.onIsPlayingChanged(isPlaying)
    }

    private fun tryToGetAudioFocus(){
        val result = mAudioManager?.requestAudioFocus(
            mOnAudioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        mCurrentAudioFocusState = if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            AUDIO_FOCUSED
        }else{
            AUDIO_NO_FOCUS_NO_DUCK
        }
    }

    private fun giveUpAudioFocus() {
        if (mAudioManager?.abandonAudioFocus(mOnAudioFocusChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK
        }
    }
    fun play() {
        player?.let {
            if (!it.isPlaying) player?.play()
        }
    }

    fun play(mediaItem: MediaItem){
        mPlayOnFocusGain = true
        tryToGetAudioFocus()
       // registerAudioNoisyReceiver()
        //releaseResources(false)
        initializePlayer()
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(USAGE_MEDIA)
            .build()

        player?.let {
            it.setAudioAttributes(audioAttributes,false)
            it.setMediaItem(mediaItem)
            it.prepare()
            it.play()
            currentItem = it.currentMediaItemIndex
        }
       // mWifiLock?.acquire()
        configurePlayerState()
    }


    companion object {
        const val UPDATE_PROGRESS_DELAY = 500L

        // The volume we set the media player to when we lose audio focus, but are
        // allowed to reduce the volume instead of stopping playback.
        private const val VOLUME_DUCK = 0.2f

        // The volume we set the media player when we have audio focus.
        private const val VOLUME_NORMAL = 1.0f

        // we don't have audio focus, and can't duck (play at a low volume)
        private const val AUDIO_NO_FOCUS_NO_DUCK = 0

        // we don't have focus, but can duck (play at a low volume)
        private const val AUDIO_NO_FOCUS_CAN_DUCK = 1

        // we have full audio focus
        private const val AUDIO_FOCUSED = 2
    }
}