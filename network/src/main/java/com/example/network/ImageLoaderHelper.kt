package com.example.network

import android.app.Activity
import android.app.Application
import android.text.TextUtils
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

object ImageLoaderHelper {
    fun loadImage(imageView: ImageView, url: String?) {
        if (imageView.context == null) {
            return
        } else if (imageView.context !is Application) {
            if (imageView.context is FragmentActivity) {
                if ((imageView.context as FragmentActivity).isDestroyed) {
                    return
                }
            } else if (imageView.context is Activity) {
                if ((imageView.context as Activity).isDestroyed) {
                    return
                }
            }
        }
        if (!TextUtils.isEmpty(url)) {
            val myOptions: RequestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            Glide.with(imageView.context)
                .load(url)
                .apply(myOptions)
                .into(imageView)
        }
    }
}