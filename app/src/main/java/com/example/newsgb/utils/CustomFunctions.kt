package com.example.newsgb.utils

fun String.formatApiStringToDate(): String = this.replace(("[a-zA-Z]").toRegex(), " ")