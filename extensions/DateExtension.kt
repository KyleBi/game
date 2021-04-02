package com.github.kotlinapp.extensions

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


fun Long.toDefaultString(
    dateFormat: Int = DateFormat.MEDIUM,
    locale: Locale = Locale.getDefault()
): String =
    DateFormat.getDateInstance(dateFormat, locale).format(this)


fun Long.toCustomString(style: String, locale: Locale = Locale.getDefault()): String =
    SimpleDateFormat(style, locale).format(this)
