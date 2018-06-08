package com.example.learn.business


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.example.learn.*
import com.oushangfeng.pinnedsectionitemdecoration.PinnedHeaderItemDecoration
import com.oushangfeng.pinnedsectionitemdecoration.utils.FullSpanUtil
import kotlinx.android.synthetic.main.merchant_page_food_layout.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MerchantFoodLayout(context: Context) : FrameLayout(context), ScrollableViewProvider {

    var topHeight: Int = 0
    var recommendHeight: Int = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.merchant_page_food_layout, this)
        initialData()
    }

    private fun initialData() {
        val foodAdapter = FoodAdapter(Data.foodDetails())
        vRecycler.addItemDecoration(PinnedHeaderItemDecoration.Builder(FoodAdapter.TYPE_TITLE)
                .setDividerId(R.drawable.transparent)
                .create())

        vRecycler.adapter = foodAdapter
        foodAdapter.openLoadAnimation()

        vRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var totalDy = 0

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                totalDy -= dy
                val transY = if (totalDy > -(topHeight + recommendHeight)) totalDy else -(topHeight + recommendHeight)
                vSide.translationY = transY.toFloat()
            }
        })

        vSide.isNestedScrollingEnabled = false
        vSide.adapter = SideAdapter(Data.foodMenus())
    }

    override fun getScrollableView(): View {
        return vRecycler
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        EventBus.getDefault().register(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleBusEvent(event: BusEvent) {
        when (event.act) {
            Bus.FOOD_TOP_HEIGHT -> {
                topHeight = event.obj as Int
                adjustSideLayoutPosition()
            }
            Bus.FOOD_RECOMMEND_HEIGHT -> {
                recommendHeight = event.obj as Int
                adjustSideLayoutPosition()
            }
        }
    }

    private fun adjustSideLayoutPosition() {
        val lp = (vSide.layoutParams as MarginLayoutParams)
        if (lp.topMargin != topHeight + recommendHeight) {
            lp.topMargin = topHeight + recommendHeight
            lp.height = screenHeight() - resDimension(R.dimen.title_height) - resDimension(R.dimen.merchant_tab_height)
            vSide.layoutParams = lp
            vSide.anim(android.R.anim.fade_in, View.VISIBLE)
        }
    }
}

class FoodTop : MultiItemEntity {
    override fun getItemType(): Int = FoodAdapter.TYPE_TOP
}

class FoodRecommend(val data: List<FoodContent>) : MultiItemEntity {
    override fun getItemType(): Int = FoodAdapter.TYPE_RECOMMEND
}

class FoodCover(val url: Int) : MultiItemEntity {
    override fun getItemType(): Int = FoodAdapter.TYPE_CONTENT_IMAGE
}

class FoodTitle(val title: String) : MultiItemEntity {
    override fun getItemType(): Int = FoodAdapter.TYPE_TITLE
}

class FoodContent(val name: String, val icon: Int, val desc: String, val price: Float) : MultiItemEntity {
    override fun getItemType(): Int = FoodAdapter.TYPE_CONTENT
}

class FoodAdapter(data: List<MultiItemEntity>) : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(data) {
    companion object {
        private var TYPE_PRODUCER = 1
        val TYPE_TOP = TYPE_PRODUCER++
        val TYPE_RECOMMEND = TYPE_PRODUCER++
        val TYPE_CONTENT_IMAGE = TYPE_PRODUCER++
        val TYPE_TITLE = TYPE_PRODUCER++
        val TYPE_CONTENT = TYPE_PRODUCER++
    }


    init {
        addItemType(TYPE_TOP, R.layout.merchant_food_list_top)
        addItemType(TYPE_RECOMMEND, R.layout.merchant_food_list_recommend)
        addItemType(TYPE_CONTENT_IMAGE, R.layout.merchant_food_list_content_image)
        addItemType(TYPE_TITLE, R.layout.merchant_food_list_sticky_title)
        addItemType(TYPE_CONTENT, R.layout.merchant_food_list_content)
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        FullSpanUtil.onAttachedToRecyclerView(recyclerView, this, TYPE_TITLE)
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        super.onViewAttachedToWindow(holder)
        FullSpanUtil.onViewAttachedToWindow(holder, this, TYPE_TITLE)
    }

    override fun convert(helper: BaseViewHolder, item: MultiItemEntity) {
        when (helper.itemViewType) {
            TYPE_TOP -> {
                helper.itemView.postDelayed1(Runnable { postEvent(Bus.FOOD_TOP_HEIGHT, helper.itemView.height) })
            }
            TYPE_RECOMMEND -> {
                helper.itemView.postDelayed1(Runnable { postEvent(Bus.FOOD_RECOMMEND_HEIGHT, helper.itemView.height) })
                val view: RecyclerView = helper.getView(R.id.vRecommends)
                view.adapter = FoodRecommendCellAdapter((item as FoodRecommend).data)
            }
            TYPE_CONTENT_IMAGE -> {
                (item as FoodCover).apply { helper.setImageUrl(R.id.vImage, url) }
            }
            TYPE_TITLE -> {
                (item as FoodTitle).apply { helper.setText(R.id.vTitle, title) }
            }
            TYPE_CONTENT -> {
                (item as FoodContent).apply {
                    helper.setText(R.id.vPrice, "￥$price")
                            .setText(R.id.vName, name)
                            .setText(R.id.vDesc, desc)
                            .setImageUrl(R.id.vIcon, icon)
                }
            }
        }
    }
}

fun BaseViewHolder.setImageUrl(viewId: Int, imageUrl: Int): BaseViewHolder {
    val view: ImageView = getView(viewId)
    view.load(imageUrl)
    return this
}

class FoodRecommendCellAdapter(data: List<FoodContent>) : BaseQuickAdapter<FoodContent, BaseViewHolder>(data) {
    init {
        mLayoutResId = R.layout.merchant_food_list_recommend_cell
    }

    override fun convert(helper: BaseViewHolder, item: FoodContent) {
        (helper.itemView.layoutParams as ViewGroup.MarginLayoutParams).leftMargin = if (mData.indexOf(item) != 0) dp(2) else 0

        helper.setText(R.id.vPrice, "￥${item.price}")
                .setText(R.id.vName, item.name)
                .setImageUrl(R.id.vImage, item.icon)
    }
}


class SideAdapter(data: List<FoodTitle>) : BaseQuickAdapter<FoodTitle, BaseViewHolder>(data) {
    init {
        mLayoutResId = R.layout.merchant_food_list_side
    }

    override fun convert(helper: BaseViewHolder, item: FoodTitle) {
        item.apply { helper.setText(R.id.vTitle, title) }
    }
}