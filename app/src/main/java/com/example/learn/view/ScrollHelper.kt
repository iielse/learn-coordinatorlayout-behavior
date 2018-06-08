package com.example.learn.view

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.example.learn.log


object ScrollHelper {

    open class OnScrollStateChangedListener {
        open fun onScrollToBottom() {}
    }

    fun init(recyclerView: RecyclerView, callback: OnScrollStateChangedListener) {
        if (recyclerView.layoutManager !is LinearLayoutManager) throw IllegalArgumentException("just support LinearLayoutManager")
        val layoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var scrollDown = false

            override fun onScrolled(vRecycler: RecyclerView?, dx: Int, dy: Int) {
                scrollDown = if (layoutManager.reverseLayout) dy < 0 else dy > 0
            }

            override fun onScrollStateChanged(vRecycler: RecyclerView?, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        val lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition()
                        val totalItemCount = layoutManager.itemCount
                        log("onScrollStateChanged  lastVisibleItem[$lastVisibleItem]  totalItemCount[$totalItemCount]  scrollDown[$scrollDown]")
                        if (lastVisibleItem >= totalItemCount - 1 && scrollDown) {
                            callback.onScrollToBottom()
                            log("onScrollStateChanged  onScrollToBottom")
                        }
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                    }
                }
            }
        })
    }

    open class OnScrollStateChangedListener2 {
        open fun onScrollToTop() {}
    }

    fun init1(recyclerView: RecyclerView, callback: OnScrollStateChangedListener2) {
        if (recyclerView.layoutManager !is LinearLayoutManager) throw IllegalArgumentException("just1 support LinearLayoutManager")
        val layoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var scrollDown = false

            override fun onScrolled(vRecycler: RecyclerView?, dx: Int, dy: Int) {
                scrollDown = if (layoutManager.reverseLayout) dy < 0 else dy > 0
            }

            override fun onScrollStateChanged(vRecycler: RecyclerView?, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        val firstVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                        log("onScrollStateChanged  firstVisibleItem[$firstVisibleItem] scrollUp[${!scrollDown}]")
                        if (firstVisibleItem <= 0 && !scrollDown) {
                            callback.onScrollToTop()
                            log("onScrollStateChanged  onScrollToTop")
                        }
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                    }
                }
            }
        })
    }
}
