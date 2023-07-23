package com.example.musicplayerapp.domain.model

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import java.io.File

data class Song(
    var id: String?,
    var songName: String?,
    var albumName: String?,
    val duration:Int?,
    var path: String?,
    val albumArt:String?
    ):Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int?,
        parcel.readString(),
        parcel.readString(),
    ) {
    }



    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(songName)
        parcel.writeString(albumName)
        parcel.writeValue(duration)
        parcel.writeString(path)
        parcel.writeString(albumArt)
    }
    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }

        fun Song.createMediaItem():MediaItem {
            val song = this
            val bundle = Bundle()
            bundle.putParcelable("song",song)
            return MediaItem.Builder().setMediaId(
                this.id?:"")
                .setUri(File(path).toUri())
                .setMimeType(MimeTypes.AUDIO_MPEG)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setAlbumTitle(songName)
                        .setAlbumArtist(albumName)
                        .setArtworkUri(File(albumArt ?: "").toUri())
                        .setExtras(bundle)
                        .build()
                ).build()
        }
        fun MediaItem.toSong():Song?{
            val bundle = this.mediaMetadata.extras
            val song = bundle?.getParcelable("song") as Song ?
            return song
        }
    }

    override fun describeContents(): Int {
        return 0
    }



}