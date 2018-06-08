package com.example.learn.business

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import com.example.learn.R
import com.example.learn.argbEvaluator
import com.example.learn.dp
import com.example.learn.resDimension
import com.example.learn.resDrawable
import kotlinx.android.synthetic.main.merchant_title_layout.view.*

class MerchantTitleLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private val adInterpolator = AccelerateDecelerateInterpolator()
    private val offsetMax = resDimension(R.dimen.merchant_offset).toFloat()
    private var offset = 0F

    private val drawableBack = resDrawable(R.mipmap.back_white)
    private val drawableSearch = resDrawable(R.mipmap.icon_search)
    private val drawableCollection = resDrawable(R.mipmap.icon_collection)
    private val drawableTogether = resDrawable(R.mipmap.icon_together)
    private val drawableMenu = resDrawable(R.mipmap.icon_menu)

    init {
        LayoutInflater.from(context).inflate(R.layout.merchant_title_layout, this)
    }

    fun effectByOffset(dy: Int): Float {
        if (dy > 0 && offset == offsetMax) return 1F
        else if (dy < 0 && offset == 0F) return 0F

        offset += dy
        if (offset > offsetMax) offset = offsetMax
        else if (offset < 0) offset = 0F

        val effect = adInterpolator.getInterpolation(offset / offsetMax)

        setBackgroundColor(argbEvaluator.evaluate(effect, Color.TRANSPARENT, 0xFFFAFAFA.toInt()) as Int)

        val e: Int = argbEvaluator.evaluate(effect, Color.WHITE, 0xFF646464.toInt()) as Int
        vBack.setImageDrawable(tintDrawable(drawableBack, ColorStateList.valueOf(e)))
        vTogether.setImageDrawable(tintDrawable(drawableTogether, ColorStateList.valueOf(e)))
        vMenu.setImageDrawable(tintDrawable(drawableMenu, ColorStateList.valueOf(e)))
        vCollection.setImageDrawable(tintDrawable(drawableCollection, ColorStateList.valueOf(e)))
        vSearch.setImageDrawable(tintDrawable(drawableSearch, ColorStateList.valueOf(e)))

        vSearch.scaleX = (1 - 0.4 * effect).toFloat()
        vSearch.scaleY = (1 - 0.4 * effect).toFloat()
        vSearch.translationX = -(vSearchBorder.width - vSearch.width + dp(3)) * effect
        vSearchBorder.alpha = effect
        vSearchBorder.pivotX = vSearchBorder.width.toFloat()
        vSearchBorder.scaleX = (0.2 + 0.8 * effect).toFloat()
        vSearchHint.alpha = effect
        vSearchHint.translationX = (vSearchHint.width / 3) * (1 - effect)

        return effect
    }

    private fun tintDrawable(drawable: Drawable, colors: ColorStateList): Drawable {
        val wrappedDrawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTintList(wrappedDrawable, colors)
        return wrappedDrawable
    }
}
