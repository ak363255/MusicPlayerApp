package com.example.recipecore.domain.utils

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

/**
 * Created by Pawan Rajotiya on 14-04-2023.
 */

// add image at a given text with given dimensions
fun SpannableString.addImage(
    atText: String,
    @DrawableRes imgSrc: Int,
    imgWidth: Int,
    imgHeight: Int,
    context: Context,
    align: Int = DynamicDrawableSpan.ALIGN_BASELINE
) {
    val drawable = context.let { ContextCompat.getDrawable(it, imgSrc) } ?: return
    drawable.mutate()
    drawable.setBounds(
        0, 0,
        imgWidth,
        imgHeight
    )
    val start = this.indexOf(atText)
    this.setSpan(
        ImageSpan(drawable, align),
        start,
        start + atText.length,
        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
    )
}

// add clickable span at given indices
fun SpannableString.addClickSpan(start: Int, end: Int, linkColor: Int,underline: Boolean = false,    context:Context,
                                 textView: TextView, callback: (() -> Unit)) {
    val clickableSpan: ClickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            callback.invoke()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            context.getApplicationContext()?.let { ds.color = ContextCompat.getColor(it, linkColor) }
            ds.isUnderlineText = underline
        }
    }
    this.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    textView.movementMethod = LinkMovementMethod.getInstance()
}

// set given style to substring
fun SpannableString.setStyleSpan(start: Int, end: Int, typeFace: Int = Typeface.BOLD) {
    this.setSpan(StyleSpan(typeFace), start, end, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
}

// change color for given substring
fun SpannableString.setColor(start: Int, end: Int, color: Int,    context:Context) {
    context.getApplicationContext()?.let {
        this.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(it, color)),
            start, end, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }

}
//Strike through text for give subString
fun SpannableString.setStrikeThrough(start:Int,end:Int,    context:Context, ){
    context.getApplicationContext()?.let {
        this.setSpan(
                StrikethroughSpan(),
                start,
                end,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

// change font for given substring
fun SpannableString.setFont(start: Int, end: Int, font: Int,context: Context) {
    context.getApplicationContext()?.let {
        this.setSpan(
            CustomTypefaceSpan("", ResourcesCompat.getFont(it, font)),
            start, end, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }

}