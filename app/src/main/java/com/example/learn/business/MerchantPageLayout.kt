package com.example.learn.business

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.learn.R
import kotlinx.android.synthetic.main.merchant_page_layout.view.*

class MerchantPageLayout(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    lateinit var pagerAdapter: MerchantPageAdapter

    init {
        LayoutInflater.from(context).inflate(R.layout.merchant_page_layout, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        pagerAdapter = MerchantPageAdapter(context)
        vPager.adapter = pagerAdapter
        vSmartTab.setViewPager(vPager)
    }

    fun canScrollVertically(): Boolean {
        val view = (pagerAdapter.getItem(vPager.currentItem) as ScrollableViewProvider).getScrollableView()
        return view.canScrollVertically(-1)
    }
}

class MerchantPageAdapter(context: Context) : PagerAdapter() {
    private val layFood = MerchantFoodLayout(context)
    private val layInfo = MerchantInfoLayout(context)
    private val layComment = MerchantCommentLayout(context)

    override fun getCount(): Int = 3
    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) = container.removeView(`object` as View)
    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "点菜"
        1 -> "评论(9999+)"
        2 -> "商家"
        else -> ""
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val content = getItem(position)
        container.addView(content)
        return content
    }

    fun getItem(position: Int): View {
        return when (position) {
            0 -> layFood
            1 -> layComment
            2 -> layInfo
            else -> throw RuntimeException("getItem error position $position")
        }
    }
}

interface ScrollableViewProvider {
    fun getScrollableView(): View
}