package com.topview.purejoy.common.widget.banner

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.topview.purejoy.common.R
import com.topview.purejoy.common.util.dpToPx
import java.lang.ref.WeakReference

/**
 * Created by giagor at 2021/11/05
 * */


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

    private var indicatorSize: Int = 0
    private var indicatorGravity: Int = 0
    private var indicatorMarginTop: Int = 0
    private var indicatorMarginBottom: Int = 0
    private var indicatorMarginLeft: Int = 0
    private var indicatorMarginRight: Int = 0
    private var indicatorSpacing: Int = 0
    private var showIndicator: Boolean = true
    private var indicatorShape: Int = 0
    private var indicatorSelectColor: Int = 0
    private var indicatorUnselectColor: Int = 0

    /**
     * 轮播图自动滚动的时间间隔
     * */
    private var autoScrollTimeSpac: Long = 0L

    /**
     * 允许轮播图自动"滚动"到下一张的一个时间间隔，这个值的主要目的是判断，用户在手动"滑动"了轮播图之后，当下一个任务到来
     * 时，是否要"自动滚动"到下一张图片
     * */
    private var allowAutoScrollSpac: Long = 0L

    init {
        initAttrs(attrs)
        initViews()
    }

    private fun initAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerView)
        indicatorSize = typedArray.getDimensionPixelSize(
            R.styleable.BannerView_indicator_size,
            dpToPx(11f).toInt()
        )
        indicatorGravity = typedArray.getInt(
            R.styleable.BannerView_indicator_gravity,
            Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        )
        indicatorMarginTop = typedArray.getDimensionPixelSize(
            R.styleable.BannerView_indicator_margin_top, 0
        )
        indicatorMarginBottom = typedArray.getDimensionPixelSize(
            R.styleable.BannerView_indicator_margin_bottom,
            dpToPx(2f).toInt()
        )
        indicatorMarginLeft = typedArray.getDimensionPixelSize(
            R.styleable.BannerView_indicator_margin_left, 0
        )
        indicatorMarginRight = typedArray.getDimensionPixelSize(
            R.styleable.BannerView_indicator_margin_right, 0
        )
        indicatorSpacing = typedArray.getDimensionPixelSize(
            R.styleable.BannerView_indicator_spacing, dpToPx(2.5f).toInt()
        )
        showIndicator = typedArray.getBoolean(
            R.styleable.BannerView_show_indicator, true
        )

        autoScrollTimeSpac = typedArray.getInt(
            R.styleable.BannerView_auto_scroll_time_spacing, 3000
        ).toLong()
        allowAutoScrollSpac = autoScrollTimeSpac * 2 / 3

        indicatorShape = typedArray.getResourceId(
            R.styleable.BannerView_indicator_shape,
            R.drawable.common_banner_indicator_select_point
        )
        indicatorSelectColor =
            typedArray.getColor(R.styleable.BannerView_indicator_select_color, 0xFFF8F8FF.toInt())
        indicatorUnselectColor =
            typedArray.getColor(R.styleable.BannerView_indicator_unselect_color, 0xFF696969.toInt())

        typedArray.recycle()
    }

    private fun initViews() {
        viewPager = ViewPager(context)
        val vpParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        addView(viewPager, vpParams)

        if (showIndicator) {
            indicatorLayout = LinearLayout(context)
            val indicatorParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = indicatorGravity
                setMargins(
                    indicatorMarginLeft,
                    indicatorMarginTop,
                    indicatorMarginRight,
                    indicatorMarginBottom
                )
            }
            addView(indicatorLayout, indicatorParams)
        }
    }

    fun setBanners(banners: List<Drawable>) {
        displayBannerCounts = banners.size

        if (showIndicator) {
            initIndicators()
        }
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
            width = indicatorSize
            height = indicatorSize
            leftMargin = indicatorSpacing
            rightMargin = indicatorSpacing
        }

        for (i in 0 until displayBannerCounts) {
            val indicator = ImageView(context)
            indicator.setImageResource(indicatorShape)
            indicatorIvs.add(indicator)
            indicatorLayout.addView(indicator, indicatorParams)
        }
        switchIndicatorTo(0)
    }

    private fun getShowImage(banners: List<Drawable>) {
        bannerItems.clear()

        val bannerFirst = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageDrawable(banners[displayBannerCounts - 1])
        }
        bannerItems.add(bannerFirst)

        for (drawable in banners) {
            val imageView = ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageDrawable(drawable)
            }
            bannerItems.add(imageView)
        }

        val bannerEnd = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageDrawable(banners[0])
        }
        bannerItems.add(bannerEnd)
    }

    private fun startSchedule() {
        scheduleHandler.postDelayed(scheduleRunnable, autoScrollTimeSpac)
    }

    /**
     * 切换指示器
     *
     * @param cur 对应的指示器图标变成 "选中"
     * */
    private fun switchIndicatorTo(cur: Int) {
        if (!showIndicator) {
            return
        }

        // 先全部置为"未选中"状态
        for (indicator in indicatorIvs) {
            indicator.setColorFilter(indicatorUnselectColor)
        }
        // 置指定项为"选中"状态
        indicatorIvs[cur].setColorFilter(indicatorSelectColor)
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

        if (showIndicator) {
            // 切换指示器
            switchIndicatorTo(curIndicator)
        }
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
            if (now - draggingTime > allowAutoScrollSpac) {
                // 正常触发任务
                val vp: ViewPager = bannerView.viewPager
                vp.currentItem = vp.currentItem + 1
            }
            // 推送任务
            startSchedule()
        }
    }
}