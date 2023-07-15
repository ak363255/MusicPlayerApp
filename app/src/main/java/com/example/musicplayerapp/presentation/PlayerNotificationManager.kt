package com.example.musicplayerapp.presentation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat.stopForeground
import androidx.core.content.getSystemService
import androidx.media3.common.MediaItem
import com.example.musicplayerapp.R
import java.io.File

class PlayerNotificationManager constructor(private val mService: PlayerService):BroadcastReceiver(){

    private val mPlayIntent:PendingIntent
    private val mPauseIntent:PendingIntent
    private val mPreviousIntent:PendingIntent
    private val mNextIntent:PendingIntent
    private val mStopIntent:PendingIntent
    private var mRemoteViews:RemoteViews? = null
    private var notificaionBuilder:NotificationCompat.Builder? = null
    private var mNotificationManager : NotificationManager? = null
    var mStarted = false
    private val mediaItem : MediaItem?
       get() = mService.getCurrentMediaItem()

    private fun getPackageName():String{
        return mService.packageName
    }

    init {
        mNotificationManager = mService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mPauseIntent = PendingIntent.getBroadcast(
            mService, NOTIFICATION_REQUEST_CODE,
            Intent(ACTION_PAUSE).setPackage(getPackageName()),PendingIntent.FLAG_UPDATE_CURRENT
        )
        mPlayIntent = PendingIntent.getBroadcast(
            mService, NOTIFICATION_REQUEST_CODE,
            Intent(ACTION_PLAY).setPackage(getPackageName()),PendingIntent.FLAG_UPDATE_CURRENT
        )
        mPreviousIntent = PendingIntent.getBroadcast(
            mService, NOTIFICATION_REQUEST_CODE,
            Intent(ACTION_PREV).setPackage(getPackageName()), PendingIntent.FLAG_CANCEL_CURRENT
        )
        mNextIntent = PendingIntent.getBroadcast(
            mService, NOTIFICATION_REQUEST_CODE,
            Intent(ACTION_NEXT).setPackage(getPackageName()), PendingIntent.FLAG_CANCEL_CURRENT
        )
        mStopIntent = PendingIntent.getBroadcast(
            mService, NOTIFICATION_REQUEST_CODE,
            Intent(ACTION_STOP).setPackage(getPackageName()), PendingIntent.FLAG_CANCEL_CURRENT
        )
        mNotificationManager?.cancelAll()
    }


    fun createMediaNotification(){
        val filter = IntentFilter().apply {
            addAction(ACTION_NEXT)
            addAction(ACTION_PAUSE)
            addAction(ACTION_PLAY)
            addAction(ACTION_PREV)
            addAction(ACTION_STOP)
        }
        mService.registerReceiver(this,filter)
        if(!mStarted){
            mStarted = true
            mService?.startForeground(NOTIFICATION_ID,generateNotification())
        }
    }

     fun generateNotification(isPlaying: Boolean? = null): Notification? {
        if(notificaionBuilder == null){
            notificaionBuilder = NotificationCompat.Builder(mService, CHANNEL_ID)
            notificaionBuilder?.setSmallIcon(R.drawable.music_icon)
                ?.setLargeIcon(BitmapFactory.decodeResource(mService.resources,R.drawable.music_icon))
                ?.setContentTitle("Music Player")
                ?.setContentText("Music Player")
                ?.setDeleteIntent(mStopIntent)
                ?.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                ?.setCategory(Notification.CATEGORY_TRANSPORT)
                ?.setOnlyAlertOnce(true)

            // Notification channels are only supported on Android O+.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            }
        }

        mRemoteViews = RemoteViews(getPackageName(),R.layout.player_notification_view)
       notificaionBuilder?.setCustomContentView(mRemoteViews)
        notificaionBuilder?.setOngoing(true)
        mRemoteViews?.let {
            createRemoteViews(it)
        }

        mService?.getCurrentMediaItem()?.mediaMetadata?.artworkUri?.let {
            val bitmap = BitmapFactory.decodeFile(File(it.path.toString()).path)
            mRemoteViews?.setImageViewBitmap(R.id.notification_image_view,bitmap)
        }?:{
            val bitmap = BitmapFactory.decodeResource(mService.resources,R.drawable.music_icon)
            mRemoteViews?.setImageViewBitmap(R.id.notification_image_view,bitmap)
        }
        if(isPlaying==true){
            showPauseIcon()
        }else{
            showPlayIcon()
        }
        mNotificationManager?.notify(NOTIFICATION_ID,notificaionBuilder?.build())
        return notificaionBuilder?.build()

    }

    private fun showPlayIcon() {
        mRemoteViews?.setViewVisibility(
            R.id.expanded_notification_pause_image_view,
            View.GONE
        )
        mRemoteViews?.setViewVisibility(
            R.id.expanded_notification_play_image_view,
            View.VISIBLE
        )
    }

    private fun showPauseIcon() {
        mRemoteViews?.setViewVisibility(
            R.id.expanded_notification_pause_image_view,
            View.VISIBLE
        )
        mRemoteViews?.setViewVisibility(
            R.id.expanded_notification_play_image_view,
            View.GONE
        )    }

    private fun createRemoteViews(remoteViews: RemoteViews) {
        remoteViews.setViewVisibility(
            R.id.expanded_notification_skip_next_image_view,
            View.VISIBLE
        )
        remoteViews.setViewVisibility(
            R.id.expanded_notification_skip_back_image_view,
            View.VISIBLE
        )
        remoteViews.setTextViewText(
            R.id.expanded_notification_song_name_text_view,
            mediaItem?.mediaMetadata?.albumTitle
        )
        remoteViews.setTextViewText(
            R.id.expanded_notification_singer_name_text_view,
            mediaItem?.mediaMetadata?.albumArtist
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        if (mNotificationManager?.getNotificationChannel(CHANNEL_ID) == null) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                mService.getString(R.string.notification_channel),
                NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.description =
                mService.getString(R.string.notification_channel_description)
            mNotificationManager?.createNotificationChannel(notificationChannel)
        }    }

    override fun onReceive(p0: Context?, intent: Intent?) {
        when (intent?.action) {
            ACTION_PAUSE -> mService.pause()
            ACTION_PLAY -> mService.play()
            ACTION_NEXT -> mService.skipToNext()
            ACTION_PREV -> mService.skipToPrevious()
            ACTION_STOP -> {
                mService.run {
                    unregisterReceiver(this@PlayerNotificationManager)
                    mService.stop()
                }
            }
        }
    }

    companion object {
        private const val ACTION_PAUSE = "app.pause"
        private const val ACTION_PLAY = "app.play"
        private const val ACTION_PREV = "app.prev"
        private const val ACTION_NEXT = "app.next"
        private const val ACTION_STOP = "app.stop"
        private const val CHANNEL_ID = "app.MUSIC_CHANNEL_ID"
        private const val NOTIFICATION_ID = 412
        private const val NOTIFICATION_REQUEST_CODE = 100
    }
}