package com.example.newsgb.utils

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.example.newsgb.R

fun String.formatApiStringToDate(): String = this.replace(("[a-zA-Z]").toRegex(), " ")

/**
 * устанавливаем цвет иконки закладки
 */
fun AppCompatImageView.setBookmarkIconColor(context: Context, isChecked: Boolean) {
    if (isChecked) {
        changeColor(this, R.color.bookmark_selected_color, context)
    } else {
        changeColor(this, R.color.bookmark_unselected_color, context)
    }
}

/**
 * метод изменения цвета иконки
 */
private fun changeColor(bookmarkIcon: AppCompatImageView, tintColor: Int, context: Context) {
    bookmarkIcon.setColorFilter(
        ContextCompat.getColor(
            context,
            tintColor
        ), android.graphics.PorterDuff.Mode.SRC_IN
    )
}