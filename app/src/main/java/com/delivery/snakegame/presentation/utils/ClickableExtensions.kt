package com.delivery.snakegame.presentation.utils

import android.os.SystemClock
import android.view.View

private const val LONG_SAFE_CLICK_DELAY = 600

fun View.setThrottleOnClickListener(listener: ((View) -> Unit)?) {
    setOnClickListener(listener?.let { ThrottleViewClickListener(it) })
}

fun View.setLongThrottleOnClickListener(listener: ((View) -> Unit)?) {
    setOnClickListener(listener?.let { ThrottleViewClickListener(it, LONG_SAFE_CLICK_DELAY) })
}

private class SafeClickHandler(private val listener: ((View) -> Unit), private val clickDelay: Int?) {
    companion object {
        private const val DEFAULT_SAFE_CLICK_DELAY = 200
    }

    private val safeClickDelay: Int
        get() = clickDelay ?: DEFAULT_SAFE_CLICK_DELAY

    var lastClickTime = 0L

    fun isCanHandleClick(currentTime: Long) = lastClickTime < (currentTime - safeClickDelay)

    fun handleClick(v: View) {
        val currentTime = SystemClock.elapsedRealtime()
        if (isCanHandleClick(currentTime)) {
            listener(v)
            lastClickTime = currentTime
        }
    }
}

class ThrottleViewClickListener(
    clickListener: ((View) -> Unit),
    clickDelay: Int? = null
) : View.OnClickListener {
    private val clickHandler = SafeClickHandler(clickListener, clickDelay)
    override fun onClick(v: View) = clickHandler.handleClick(v)
}
