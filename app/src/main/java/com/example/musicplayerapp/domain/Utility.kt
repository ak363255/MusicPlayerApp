package com.example.musicplayerapp.domain

import android.os.Build
import android.util.Log
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import javax.security.auth.callback.Callback
import kotlin.math.abs

object Utility {

    inline fun <T> sdk29AndUp(onSdk29: () -> T): T? {
        return if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            onSdk29()
        } else null
    }

    fun getSeekPosition(currentTime:Long,totalTime:Long):Int{
        val percent = (currentTime*1.0/totalTime*1.0)*100
        val totalMin = totalTime/TimeUnit.MINUTES.toMillis(1)
        val timeLapsed = (percent/100)*totalMin*1.0
        Log.d("duration","total min "+totalMin+" per "+Math.ceil(timeLapsed).toInt())
        return Math.ceil(timeLapsed).toInt()
    }

    fun formatTimeInMillisToString(time: Long): String {
        var timeInMillis = time
        var sign = ""
        if (timeInMillis < 0) {
            sign = "-"
            timeInMillis = abs(timeInMillis)
        }
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time)//timeInMillis / TimeUnit.MINUTES.toMillis(1)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time - TimeUnit.MINUTES.toMillis(minutes))//timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1)
        val formatted = StringBuilder(20).apply {
            append(sign)
            append(String.format("%02d", minutes))
            append(String.format(":%02d", seconds))
        }
        return try {
            String(formatted.toString().toByteArray(), Charset.forName("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            "00:00"
        }
    }
    fun Boolean?.orFalse() : Boolean = this ?: false
}