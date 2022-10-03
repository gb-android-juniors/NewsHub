package com.example.newsgb.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
        R.color.bookmark_selected_color
    } else {
        R.color.bookmark_unselected_color
    }
    bookmarkImage.setColorFilter(ContextCompat.getColor(context, tintColor), android.graphics.PorterDuff.Mode.SRC_IN)
}

internal fun Fragment.hideKeyboard() {
    view?.let { view ->
        val imm = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

/**
 * Метод для получения интента отправки ссылки на новость посредством мессенджеров и смс
 */
fun getShareNewsIntent(contentUrl:String): Intent? {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, contentUrl)
    }
    return Intent.createChooser(shareIntent, "Sharing something.")
}

/**
 * Метод для получения интента отправки email письма с обратной связью разработчикам.
 */
fun getEmailSendingIntent(emails: Array<String>, subject: String): Intent? {
    val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:") // only email apps should handle this
        putExtra(Intent.EXTRA_EMAIL, emails)
        putExtra(Intent.EXTRA_SUBJECT, subject)
    }
    return Intent.createChooser(mailIntent, "Send mail...")
}

/**
 * Метод верификации языка запроса поиска статей в API, в зависимости от выбранного региона новостей
 */
fun verifySearchLanguage(selectedCountryCode: String?) : String = when(selectedCountryCode) {
    "ar", "de", "en", "es", "fr", "he", "it", "nl", "no", "pt", "ru", "sv", "ud", "zh" -> selectedCountryCode
    else -> "en"
}