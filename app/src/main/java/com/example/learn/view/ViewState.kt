package com.example.learn.view

import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator

class ViewState {
    var topMargin: Int = 0
    var bottomMargin: Int = 0
    var leftMargin: Int = 0
    var rightMargin: Int = 0
    var width: Int = 0
    var height: Int = 0
    var translationX: Float = 0F
    var translationY: Float = 0F
    var scaleX: Float = 0F
    var scaleY: Float = 0F
    var rotation: Float = 0F
    var alpha: Float = 0F

    fun sx(scaleX: Float): ViewState {
        this.scaleX = scaleX
        return this
    }

    fun sxBy(value: Float): ViewState {
        this.scaleX *= value
        return this
    }

    fun sy(scaleY: Float): ViewState {
        this.scaleY = scaleY
        return this
    }

    fun syBy(value: Float): ViewState {
        this.scaleY *= value
        return this
    }

    fun a(alpha: Float): ViewState {
        this.alpha = alpha
        return this
    }

    fun w(width: Int): ViewState {
        this.width = width
        return this
    }


    fun h(height: Int): ViewState {
        this.height = height
        return this
    }

    fun r(rotation: Float): ViewState {
        this.rotation = rotation
        return this
    }

    fun ws(s: Float): ViewState {
        this.width = (width * s).toInt()
        return this
    }

    fun hs(s: Float): ViewState {
        this.height = (height * s).toInt()
        return this
    }

    fun tx(translationX: Float): ViewState {
        this.translationX = translationX
        return this
    }

    fun ty(translationY: Float): ViewState {
        this.translationY = translationY
        return this
    }

    fun ml(leftMargin: Int): ViewState {
        this.leftMargin = leftMargin
        return this
    }

    fun mr(rightMargin: Int): ViewState {
        this.rightMargin = rightMargin
        return this
    }

    fun mt(topMargin: Int): ViewState {
        this.topMargin = topMargin
        return this
    }

    fun mn(bottomMargin: Int): ViewState {
        this.bottomMargin = bottomMargin
        return this
    }

    fun copy(view: View): ViewState {
        this.width = view.width
        this.height = view.height
        this.translationX = view.translationX
        this.translationY = view.translationY
        this.scaleX = view.scaleX
        this.scaleY = view.scaleY
        this.rotation = view.rotation
        this.alpha = view.alpha

        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.let {
            this.topMargin = it.topMargin
            this.bottomMargin = it.bottomMargin
            this.leftMargin = it.leftMargin
            this.rightMargin = it.rightMargin
        }

        return this
    }
}

fun View.stateSet(tag: Int, vs: ViewState) {
    setTag(tag, vs)
}

fun View.stateRead(tag: Int): ViewState? {
    return getTag(tag) as? ViewState
}

fun View.stateSave(tag: Int): ViewState {
    val vs = stateRead(tag) ?: ViewState()
    vs.copy(this)
    stateSet(tag, vs)
    return vs
}

fun View.stateRefresh(tag1: Int, tag2: Int, p: Float) {
    if (this is AnimationUpdateListener) {
        onAnimationUpdate(tag1, tag2, p)
    } else {
        val vs1 = stateRead(tag1)
        val vs2 = stateRead(tag2)
        if (vs1 != null && vs2 != null) {
            if (vs1.translationX != vs2.translationX) translationX = vs1.translationX + (vs2.translationX - vs1.translationX) * p
            if (vs1.translationY != vs2.translationY) translationY = vs1.translationY + (vs2.translationY - vs1.translationY) * p
            if (vs1.scaleX != vs2.scaleX) scaleX = vs1.scaleX + (vs2.scaleX - vs1.scaleX) * p
            if (vs1.scaleY != vs2.scaleY) scaleY = vs1.scaleY + (vs2.scaleY - vs1.scaleY) * p
            if (vs1.rotation != vs2.rotation) rotation = (vs1.rotation + (vs2.rotation - vs1.rotation) * p) % 360
            if (vs1.alpha != vs2.alpha) alpha = vs1.alpha + (vs2.alpha - vs1.alpha) * p

            val o = layoutParams
            var lpChanged = false
            if (vs1.width != vs2.width) {
                o.width = (vs1.width + (vs2.width - vs1.width) * p).toInt()
                lpChanged = true
            }
            if (vs1.height != vs2.height) {
                o.height = (vs1.height + (vs2.height - vs1.height) * p).toInt()
                lpChanged = true
            }
            (o as? ViewGroup.MarginLayoutParams)?.let {
                if (vs1.topMargin != vs2.topMargin) {
                    it.topMargin = (vs1.topMargin + (vs2.topMargin - vs1.topMargin) * p).toInt()
                    lpChanged = true
                }
                if (vs1.bottomMargin != vs2.bottomMargin) {
                    it.bottomMargin = (vs1.bottomMargin + (vs2.bottomMargin - vs1.bottomMargin) * p).toInt()
                    lpChanged = true
                }
                if (vs1.leftMargin != vs2.leftMargin) {
                    it.leftMargin = (vs1.leftMargin + (vs2.leftMargin - vs1.leftMargin) * p).toInt()
                    lpChanged = true
                }
                if (vs1.topMargin != vs2.topMargin) {
                    it.topMargin = (vs1.topMargin + (vs2.topMargin - vs1.topMargin) * p).toInt()
                    lpChanged = true
                }
            }
            if (lpChanged) layoutParams = o
        }
    }
}

fun Any?.statesChangeByAnimation(views: Array<View>, tag1: Int, tag2: Int, start: Float = 0F, end: Float = 1F, updateCallback: AnimationUpdateListener? = null, updateStateListener: AnimatorListenerAdapter? = null, duration1: Long = 400L, startDelay1: Long = 0L): ValueAnimator {
    return ValueAnimator.ofFloat(start, end).apply {
        startDelay = startDelay1
        duration = duration1
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener { animation ->
            val p = animation.animatedValue as Float
            updateCallback?.onAnimationUpdate(tag1, tag2, p)
            for (it in views) it.stateRefresh(tag1, tag2, animation.animatedValue as Float)
        }
        updateStateListener?.let { addListener(it) }

        start()
    }
}

interface AnimationUpdateListener {
    fun onAnimationUpdate(tag1: Int, tag2: Int, p: Float)
}