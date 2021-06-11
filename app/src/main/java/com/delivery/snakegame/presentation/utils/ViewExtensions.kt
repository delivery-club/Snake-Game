package com.delivery.snakegame.presentation.utils

import android.content.Context
import android.graphics.Point
import android.view.View
import android.view.WindowManager

fun View.getScreenWidth(): Int {
    val display =
        (this.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val size = Point()
    display.getSize(size)
    return size.x
}

fun View.getScreenHeight(): Int {
    val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val size = Point()
    display.getSize(size)
    return size.y
}