package com.topview.purejoy.common.widget.banner

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
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
private const val autoScrollTimeSpac = 1500L

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
        scheduleHandler.postDelayed(scheduleRunnable, autoScrollTimeSpac)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {
        when (state) {
            ViewPager.SCROLL_STATE_DRAGGING -> {
                isDragging = true
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

            // 如果用户没有在"拖拽"轮播图，那么就切换到下一张
            if (!isDragging) {
                val vp: ViewPager = bannerView.viewPager
                vp.currentItem = vp.currentItem + 1
            }
            // 推送任务
            startSchedule()
        }
    }
}