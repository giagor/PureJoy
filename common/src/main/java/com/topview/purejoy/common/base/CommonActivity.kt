package com.topview.purejoy.common.base

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.topview.purejoy.common.entity.TextSizeScale
import com.topview.purejoy.common.util.PreferenceKey


/**
 * @author lanlin-code
 * 通用Activity
 */
abstract class CommonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()
        supportActionBar?.hide()
    }

    override fun attachBaseContext(newBase: Context?) {
        var context = newBase
        newBase?.let {
            // 缓存原来的字体放大倍数
            val originScale: Float
            it.resources.configuration.apply {
                originScale = fontScale
                fontScale = if (allowAdjustFontScale()) {
                    PreferenceKey.globalTextSizeScale
                } else {
                    TextSizeScale.STANDARD.scale
                }
                // 仅当字体放大倍数被真正改变时，更新字体大小
                if (originScale != fontScale) {
                    // 根据版本不同使用不同的策略更新字体大小
                    context = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        createFontScaleContext(it)
                    } else {
                        changeFontSize(it)
                    }
                }
            }
        }
        super.attachBaseContext(context)
    }

    protected open fun setContentView() {
        setContentView(getLayoutId())
    }

    /**
     * 当前Activity布局文件的ID
     * */
    protected abstract fun getLayoutId(): Int

    /**
     * @param frameLayoutId 容器的ID
     * @param targetFragment 要添加到FragmentManager并显示的Fragment
     * @param hideFragment 当前正在显示的Fragment
     * 添加Fragment到容器中并显示它
     */
    protected fun addAndShowFragment(
        frameLayoutId: Int,
        targetFragment: Fragment,
        hideFragment: Fragment
    ) {
        beginTransaction().hide(hideFragment).add(
            frameLayoutId,
            targetFragment, targetFragment::class.java.simpleName
        ).show(targetFragment).commit()
    }

    /**
     * @param containerId FrameLayout的ID
     * @param fragment 要添加到容器中的Fragment
     * 添加Fragment到容器中
     */
    protected fun addFragment(containerId: Int, fragment: Fragment) {
        beginTransaction().add(
            containerId, fragment,
            fragment::class.java.simpleName
        ).commit()
    }

    /**
     * @param fragment 要从容器中移除的Fragment
     * 移除Fragment
     */
    protected fun removeFragment(fragment: Fragment) {
        beginTransaction().remove(fragment).commit()
    }

    /**
     * @param tag 添加Fragment到容器中时所用的标识符
     * @return 如果容器中存在这样的Fragment，则将其返回，否则返回null
     */
    protected fun findFragment(tag: String?): Fragment? {
        return supportFragmentManager.findFragmentByTag(tag)
    }

    /**
     * 开启FragmentManager事务
     */
    protected fun beginTransaction(): FragmentTransaction {
        return supportFragmentManager.beginTransaction()
    }

    /**
     * @param fragment 当前正在显示的Fragment
     * 隐藏这个Fragment
     */
    protected fun hide(fragment: Fragment) {
        beginTransaction().hide(fragment).commit()
    }

    /**
     * @param fragment 要显示的Fragment
     * 显示Fragment
     */
    protected fun show(fragment: Fragment) {
        beginTransaction().show(fragment).commit()
    }

    /**
     * 是否允许放大该Activity内控件的字体大小，默认不允许。允许修改字体大小的界面应当重写该方法
     */
    protected open fun allowAdjustFontScale() = false

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createFontScaleContext(context: Context): Context =
        context.createConfigurationContext(context.resources.configuration)


    private fun changeFontSize(context: Context): Context {
        context.resources.updateConfiguration(
            context.resources.configuration,
            context.resources.displayMetrics
        )
        return context
    }
}