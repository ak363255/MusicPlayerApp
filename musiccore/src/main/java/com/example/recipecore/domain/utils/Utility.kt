package com.example.recipecore.domain.utils

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.recipecore.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object Utility {

    fun getCustomGradient(cornerRadius: Int, color: String): GradientDrawable {
        val shapeGrey = GradientDrawable()
        shapeGrey.cornerRadius = dpToPx(cornerRadius).toFloat()
        shapeGrey.color = ColorStateList.valueOf(Color.parseColor(color))
        return shapeGrey
    }

    fun <T> debounce(
        waitMs: Long,
        scope: CoroutineScope,
        destination: (T) -> Unit
    ): (T) -> Unit {
        var job: Job? = null
        return { param: T ->
            job?.cancel()
            job = scope.launch {
                delay(waitMs)
                destination(param)
            }
        }

    }

    // todo get custom gradient with custom border and solid color
    fun getCustomGradient(
        cornerRadius: Int,
        stroke: Int,
        strokeColor: String,
        color: String
    ): GradientDrawable {
        val shapeGrey = GradientDrawable()
        shapeGrey.cornerRadius = dpToPx(cornerRadius).toFloat()
        shapeGrey.setStroke(dpToPx(stroke), Color.parseColor(strokeColor))
        shapeGrey.color = ColorStateList.valueOf(Color.parseColor(color))
        return shapeGrey
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}