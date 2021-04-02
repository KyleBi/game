package com.github.kotlinapp.extensions

import android.content.Context
import androidx.core.content.ContextCompat

fun Context.color(res: Int) = ContextCompat.getColor(this, res)
