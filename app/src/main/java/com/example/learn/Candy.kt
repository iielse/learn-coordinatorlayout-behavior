package com.example.learn

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BitmapTransformation
import org.greenrobot.eventbus.EventBus

val dispatcher = Handler(Looper.getMainLooper())
val crossFade = DrawableTransitionOptions().crossFade(200)
val centerCrop = RequestOptions().centerCrop().placeholder(android.R.color.transparent).priority(Priority.NORMAL)
val argbEvaluator = android.animation.ArgbEvaluator()

fun ImageView?.load(res: Any?, transform: List<BitmapTransformation>? = null) {
    if (this == null) return
    context.activity()?.let {
        val requestOptions = if (transform != null) RequestOptions.bitmapTransform(MultiTransformation(transform)) else centerCrop
        Glide.with(it).load(when (res) {
            is Int?, is String?, is Uri?, is Drawable? -> res
            else -> throw IllegalArgumentException("imageView load un support res type")
        }).apply(requestOptions).transition(crossFade).into(this)
    }
}

fun TextView?.text(res: Any?) {
    if (this == null) return
    text = when (res) {
        is Int -> resString(res)
        is String -> res
        else -> throw IllegalArgumentException("imageView load un support res type")
    }
}


fun Context?.activity(): Activity? {
    if (this == null) return null
    if (this.javaClass.name.contains("com.android.internal.policy.DecorContext")) {
        try {
            val field = this.javaClass.getDeclaredField("mPhoneWindow")
            field.isAccessible = true
            val obj = field.get(this)
            val m1 = obj.javaClass.getMethod("getContext")
            return (m1.invoke(obj)) as Activity
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

fun View?.activity(): Activity? {
    if (this == null) return null
    return context.activity()
}

fun Activity?.v4(): FragmentActivity? {
    if (this == null) return null
    return this as? FragmentActivity
}

fun Any.resDrawable(drawableRes: Int, scale: Float = 1F): Drawable =
        ContextCompat.getDrawable(App.get(), drawableRes)!!.apply {
            setBounds(0, 0, (minimumWidth * scale).toInt(), (minimumHeight * scale).toInt())
        }

fun Any.resString(stringRes: Int): String = App.get().getString(stringRes)
fun Any.resDimension(dimensionRes: Int): Int = App.get().resources.getDimensionPixelSize(dimensionRes)

fun Any.dp(dp: Int): Int {
    val dm = DisplayMetrics()
    (App.get().getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .defaultDisplay.getMetrics(dm)
    return (dp * dm.density + 0.5f).toInt()
}

fun Any.screenWidth(): Int {
    val dm = DisplayMetrics()
    (App.get().getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .defaultDisplay.getMetrics(dm)
    return dm.widthPixels
}

fun Any.screenHeight(): Int {
    val dm = DisplayMetrics()
    (App.get().getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .defaultDisplay.getMetrics(dm)
    return dm.heightPixels
}

@SuppressLint("PrivateApi")
fun Any.statusBarHeight(): Int {
    var statusHeight = -1
    try {
        val clazz = Class.forName("com.android.internal.R\$dimen")
        val `object` = clazz.newInstance()
        val height = Integer.parseInt(clazz.getField("status_bar_height").get(`object`).toString())
        statusHeight = App.get().resources.getDimensionPixelSize(height)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return statusHeight
}

fun Any.toast(content: String) {
    Toast.makeText(App.get(), content, Toast.LENGTH_SHORT).show()
}

fun Activity.postDelayed1(r: Runnable, delayMillis: Long = 0) {
    dispatcher.postDelayed({
        if (isFinishing) return@postDelayed
        r.run()
    }, delayMillis)
}

fun View.postDelayed1(r: Runnable, delayMillis: Long = 0) {
    activity()?.postDelayed1(r, delayMillis)
}

fun Any.log(content: String, lv: Int = Log.DEBUG) {
    when (lv) {
        Log.DEBUG -> Log.d("candy", content)
        Log.ERROR -> Log.e("candy", content)
    }
}

fun Any.postEvent(a: Int, o: Any? = null, o2: Any? = null, o3: Any? = null) {
    when {
        o != null && o2 != null && o3 != null -> EventBus.getDefault().post(BusEvent(a, o, o2, o3))
        o != null && o2 != null -> EventBus.getDefault().post(BusEvent(a, o, o2))
        o != null -> EventBus.getDefault().post(BusEvent(a, o))
        else -> EventBus.getDefault().post(BusEvent(a))
    }
}

fun View.anim(animRes: Int, visible: Int = View.VISIBLE) {
    if (visibility != visible) {
        visibility = visible
        startAnimation(AnimationUtils.loadAnimation(App.get(), animRes))
    }
}