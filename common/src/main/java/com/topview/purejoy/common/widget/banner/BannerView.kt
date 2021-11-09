package com.topview.purejoy.common.widget.banner

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
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
 *
 * 使用方式：实体类 继承 BannerItem，实现具体的 View的 加载与显示逻辑
 * 自定义属性：
 * auto_scroll_time_spacing 轮播的时间
 * indicator_shape 指示器的图案
 * indicator_select_color 指示器选中时的颜色
 * indicator_unselect_color 指示器未选中颜色
 * indicator_size 指示器的大小
 * indicator_spacing 指示器之间的左右间隔
 * indicator_margin_xxx 指示器距离外部的margin
 * indicator_gravity 指示器的位置
 * show_indicator 是否显示指示器
 *
 * todo 支持多次setBanners的调用
 * */

private const val TAG = "BannerView"

class BannerView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    FrameLayout(context, attrs, defStyleAttr), ViewPager.OnPageChangeListener {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private lateinit var viewPager: ViewPager

    /**
     * 用于存放指示器
     * */
    private lateinit var indicatorLayout: LinearLayout

    private val adapter: BannerAdapter = BannerAdapter()

    /**
     * 获取主线程的Handler，用于控制图片的自动滚动
     * */
    private val scheduleHandler: Handler = Handler(Looper.getMainLooper())

    /**
     * 控制BannerView自动滚动的任务
     * */
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

    /**
     * 自定义属性
     * */
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
        // 配置ViewPager
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(this)
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

    /**
     * 提供给外界调用，外界通过该方法，设置要显示的BannerItem
     * */
    fun setBanners(items: List<BannerItem>) {
        if (items.isEmpty()) {
            return
        }

        // 适配器中设置BannerItem
        adapter.setBanners(items)
        // 判断是否要显示"指示器"
        if (showIndicator && !adapter.isSingleImage()) {
            initIndicators()
        }

        // 如果不止一张图片，那么就开启"自动滚动"的任务
        if (!adapter.isSingleImage()) {
            viewPager.currentItem = 1
            startSchedule()
        }
    }

    /**
     * 初始化指示器
     * */
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

        for (i in 0 until adapter.getDisplayCounts()) {
            val indicator = ImageView(context)
            indicator.setImageResource(indicatorShape)
            indicatorIvs.add(indicator)
            indicatorLayout.addView(indicator, indicatorParams)
        }
        // 选中第0个指示器
        switchIndicatorTo(0)
    }

    private fun startSchedule() {
        if (!adapter.isSingleImage()) {
            // 通过Handler推送延迟任务
            scheduleHandler.postDelayed(scheduleRunnable, autoScrollTimeSpac)
        }
    }

    /**
     * 切换指示器
     *
     * @param cur 对应的指示器图标变成 "选中"
     * */
    private fun switchIndicatorTo(cur: Int) {
        if (!showIndicator || adapter.isSingleImage()) {
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
            0 -> adapter.getSize() - 3
            adapter.getSize() - 1 -> 0
            else -> position - 1
        }

        if (showIndicator && !adapter.isSingleImage()) {
            // 切换指示器
            switchIndicatorTo(curIndicator)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        when (state) {
            ViewPager.SCROLL_STATE_DRAGGING -> {
                // 记录用户的「拖拽状态」及「拖拽时间」
                isDragging = true
                draggingTime = System.currentTimeMillis()
            }

            ViewPager.SCROLL_STATE_IDLE -> {
                isDragging = false

                // 获取当前ViewPager的位置
                val curPosition = viewPager.currentItem
                if (!adapter.isSingleImage()) {
                    // 如果滚动到"最后1张"(假图)，迅速切换到"第1张"，让用户认为当前看的是"第1张"
                    if (curPosition == adapter.getSize() - 1) {
                        viewPager.setCurrentItem(1, false)
                    } else if (curPosition == 0) {
                        // 如果滚动到"第0张"(假图)，迅速切换到"最后1张"，让用户认为当前看的是"最后1张"
                        viewPager.setCurrentItem(adapter.getSize() - 2, false)
                    }
                }
            }
        }
    }

    private inner class BannerAdapter : PagerAdapter() {
        /**
         * 存放BannerItem，内容为："最后一个Banner" + "用户看得见的Banner" + "第一个Banner"
         * */
        private val bannerItems: MutableList<BannerItem> = mutableListOf()

        /**
         * 存放BannerItem对应的View，是bannerItems对应的View
         * */
        private lateinit var bannerViews: Array<View?>

        override fun getCount(): Int {
            return bannerItems.size
        }

        override fun isViewFromObject(view: View, any: Any): Boolean {
            return view === any
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            if (bannerViews[position] === null) {
                bannerViews[position] =
                    bannerItems[position].onCreateView(LayoutInflater.from(context), container)
            }
            container.addView(bannerViews[position])

            return bannerViews[position]!!
        }

        override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
            container.removeView(any as View)
        }

        fun setBanners(items: List<BannerItem>) {
            if (items.isEmpty()) {
                return
            }

            // 存储BannerItem
            bannerItems.clear()

            if (items.size != 1) {
                bannerItems.add(items.last())
            }

            for (item in items) {
                bannerItems.add(item)
            }

            if (items.size != 1) {
                bannerItems.add(items.first())
            }

            // 创建存储View的数组，用于保存BannerItem对应的View
            bannerViews = arrayOfNulls(bannerItems.size)

            // 通知数据已经改变
            notifyDataSetChanged()
        }

        /**
         * 获取bannerItems的大小
         * */
        fun getSize(): Int = bannerItems.size

        /**
         * 获取展示的Banner的数量，即用户看得到的Banner的数量
         * */
        fun getDisplayCounts(): Int {
            return when (getSize()) {
                0 -> 0
                1 -> 1
                else -> getSize() - 2
            }
        }

        /**
         * 表示是否是「单张图片」
         * */
        fun isSingleImage(): Boolean = if (getSize() == 1) true else false
    }

    private inner class ScheduleRunnable(bannerView: BannerView) : Runnable {

        /**
         * 通过"弱引用"的方式持有BannerView，有效地防止内存泄漏
         * */
        private val viewWef = WeakReference(bannerView)

        override fun run() {
            val bannerView: BannerView? = viewWef.get()
            // 为null，说明BannerView已经被GC，直接return
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