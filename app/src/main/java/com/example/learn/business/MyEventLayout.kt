package com.example.learn.business

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout

class MyEventLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private var needDispatchDown = false
    private var mLastMoveEvent: MotionEvent? = null

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return true
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                needDispatchDown = true
            }
            MotionEvent.ACTION_MOVE -> {
                dispatchHorizontalTouchEventToChild(ev)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                needDispatchDown = false
            }
        }

        mLastMoveEvent = ev
        return true
    }

    private fun dispatchHorizontalTouchEventToChild(current: MotionEvent) {
        mLastMoveEvent?.apply {
            if (needDispatchDown) {
                val d = MotionEvent.obtain(downTime, eventTime + ViewConfiguration.getLongPressTimeout(), MotionEvent.ACTION_DOWN, current.x, y, metaState)
                getChildAt(0).onTouchEvent(d)
                needDispatchDown = false
            }
            val e = MotionEvent.obtain(downTime, eventTime + ViewConfiguration.getLongPressTimeout(), MotionEvent.ACTION_MOVE, current.x, y, metaState)
            getChildAt(0).onTouchEvent(e)
        }
    }
}