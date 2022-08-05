package com.example.newsgb.utils

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.example.newsgb.R

/**
 * Метод для форматирования строки с датой, приходящей из API
 */
fun String.formatApiStringToDate(): String = this.replace(("[a-zA-Z]").toRegex(), " ")

/**
 * Метод для установки цвета для флажка закладки
 */
fun setBookmarkIconColor(context: Context, bookmarkImage: AppCompatImageView, isChecked: Boolean) {
    val tintColor = if (isChecked) {
        R.color.indigo_100
    } else {
        R.color.bookmark_unselected_color
    }
    bookmarkImage.setColorFilter(ContextCompat.getColor(context, tintColor), android.graphics.PorterDuff.Mode.SRC_IN)
}