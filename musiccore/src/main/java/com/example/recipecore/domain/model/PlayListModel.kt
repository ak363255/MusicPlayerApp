package com.example.recipecore.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.example.musicplayerapp.domain.model.Song

class PlayListModel() :Parcelable{
    var playListName:String? = null
    var createdBy:String? = null
    var playList:List<Song>? = null
    var id:Int? = null

    constructor(parcel: Parcel) : this() {
        playListName = parcel.readString()
        createdBy = parcel.readString()
        playList = parcel.createTypedArrayList(Song)
        id = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(playListName)
        parcel.writeString(createdBy)
        parcel.writeTypedList(playList)
        parcel.writeValue(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlayListModel> {
        override fun createFromParcel(parcel: Parcel): PlayListModel {
            return PlayListModel(parcel)
        }

        override fun newArray(size: Int): Array<PlayListModel?> {
            return arrayOfNulls(size)
        }
    }
}