package com.topview.purejoy.common.widget.banner

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.topview.purejoy.common.R
import java.lang.ref.WeakReference

/**
 * Created by giagor at 2021/11/05
 * */

/**
 * 轮播图自动滚动的时间间隔
 * */
private const val AUTO_SCROLL_TIME_SPAC = 1500L

/**
 * 允许轮播图自动"滚动"到下一张的一个时间间隔，这个值的主要目的是判断，用户在手动"滑动"了轮播图之后，当下一个任务到来
 * 时，是否要"自动滚动"到下一张图片
 * */
private const val ALLOW_AUTO_SCROLL_SPAC = AUTO_SCROLL_TIME_SPAC - 500L

private const val TAG = "BannerView"

class BannerView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs),
    ViewPager.OnPageChangeListener {

    private lateinit var viewPager: ViewPager

    private lateinit var indicatorLayout: LinearLayout

    /**
     * 存放要展示的View，内容为："最后一个Banner" + "用户看得见的Banner" + "第一个Banner"
     * */
    private val bannerItems: MutableList<View> = mutableListOf()

    /**
     * 用户看到的"Banner"的数量
     * */
    private var displayBannerCounts: Int = 0

    /**
     * 获取主线程的Handler，用于控制图片的自动滚动
     * */
    private val scheduleHandler: Handler = Handler(Looper.getMainLooper())

    private val scheduleRunnable = ScheduleRunnable(this)

    /**
     * 表明当前用户是否正在"拖拽"轮播图
     * */
    private var isDragging: Boolean = false

    /**
     * 记录用户"拖拽"轮播图的时间，可以利用该值，判断是否要让轮播图的"自动滚动"到下一张图片
     * */
    private var draggingTime: Long = 0

    /**
     * 存放"指示器"对应的ImageView
     * */
    private var indicatorIvs = mutableListOf<ImageView>()

    init {
        initViews()
    }

    private fun initViews() {
        viewPager = ViewPager(context)
        indicatorLayout = LinearLayout(context)

        val vpParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val indicatorParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        indicatorParams.apply {
//            gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        }

        addView(viewPager, vpParams)
        addView(indicatorLayout, indicatorParams)
    }

    fun setBanners(banners: List<Drawable>) {
        displayBannerCounts = banners.size

        initIndicators()
        getShowImage(banners)

        val adapter = BannerAdapter()
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(this)
        viewPager.currentItem = 1

        startSchedule()
    }

    private fun initIndicators() {
        val indicatorParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
//            width = 20
//            height = 20
            leftMargin = 10
            rightMargin = 10
        }

        for (i in 0 until displayBannerCounts) {
            val indicator = ImageView(context)
            if (i == 0) {
                indicator.setBackgroundResource(R.drawable.common_banner_def_indicator_select)
            } else {
                indicator.setBackgroundResource(R.drawable.common_banner_def_indicator_unselect)
            }
            indicatorIvs.add(indicator)
            indicatorLayout.addView(indicator, indicatorParams)
        }
    }

    private fun getShowImage(banners: List<Drawable>) {
        bannerItems.clear()
        bannerItems.add(ImageView(context).apply { setImageDrawable(banners[displayBannerCounts - 1]) })
        for (drawable in banners) {
            val imageView = ImageView(context)
            imageView.setImageDrawable(drawable)
            bannerItems.add(imageView)
        }
        bannerItems.add(ImageView(context).apply { setImageDrawable(banners[0]) })
    }

    private fun startSchedule() {
        scheduleHandler.postDelayed(scheduleRunnable, AUTO_SCROLL_TIME_SPAC)
    }

    /**
     * 切换指示器
     *
     * @param cur 对应的指示器图标变成 "选中"
     * */
    private fun switchIndicator(cur: Int) {
        // 先全部置为"未选中"状态
        for (indicator in indicatorIvs) {
            indicator.setBackgroundResource(R.drawable.common_banner_def_indicator_unselect)
        }
        // 置指定项为"选中"状态
        indicatorIvs[cur].setBackgroundResource(R.drawable.common_banner_def_indicator_select)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        // 根据不同的情况，获取当前的指示器的下标
        val curIndicator = when (position) {
            0 -> bannerItems.size - 3
            bannerItems.size - 1 -> 0
            else -> position - 1
        }

        // 切换指示器
        switchIndicator(curIndicator)
    }

    override fun onPageScrollStateChanged(state: Int) {
        when (state) {
            ViewPager.SCROLL_STATE_DRAGGING -> {
                isDragging = true
                draggingTime = System.currentTimeMillis()
            }

            ViewPager.SCROLL_STATE_IDLE -> {
                isDragging = false

                val curPosition = viewPager.currentItem // 获取当前ViewPager的位置
                if (curPosition == bannerItems.size - 1) {
                    viewPager.setCurrentItem(1, false)
                } else if (curPosition == 0) {
                    viewPager.setCurrentItem(bannerItems.size - 2, false)
                }
            }
        }
    }

    inner class BannerAdapter : PagerAdapter() {
        override fun getCount(): Int {
            return bannerItems.size
        }

        override fun isViewFromObject(view: View, any: Any): Boolean {
            return view === any
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val item = bannerItems[position]
            container.addView(item)
            return item
        }

        override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
            container.removeView(any as View)
        }
    }

    inner class ScheduleRunnable(bannerView: BannerView) : Runnable {

        private val viewWef = WeakReference(bannerView)

        override fun run() {
            val bannerView: BannerView? = viewWef.get()
            if (bannerView === null) {
                return
            }

            // 如果任务触发时，用户正在"拖拽"轮播图，那么就取消此次图片的"自动滚动"，推送下一次的任务
            if (isDragging) {
                // 推送任务
                startSchedule()
                return
            }

            val now = System.currentTimeMillis()
            // 判断用户前一个阶段周期中是否有去"拖拽"轮播图，以及计算 "现在 - 拖拽轮播图" 的时间差
            if (now - draggingTime > ALLOW_AUTO_SCROLL_SPAC) {
                // 正常触发任务
                val vp: ViewPager = bannerView.viewPager
                vp.currentItem = vp.currentItem + 1
            }
            // 推送任务
            startSchedule()
        }
    }
}