package com.example.learn.business

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.example.learn.R
import com.example.learn.log
import kotlinx.android.synthetic.main.merchant_page_cell_layout.view.*

class MerchantInfoLayout(context: Context) : FrameLayout(context), ScrollableViewProvider {
    override fun getScrollableView(): View {
        return vRecycler
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.merchant_page_cell_layout, this)
        initialData()
    }

    private fun initialData() {
    }
}