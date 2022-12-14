package com.app.subastas.view.util

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager


class CustomViewPager(context: Context?, attrs: AttributeSet?) :
    ViewPager(context!!, attrs) {

    private var enable = true

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (enable) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (enable) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.enable = enabled
    }
}