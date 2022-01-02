package com.topview.purejoy.common.base

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.topview.purejoy.common.util.StatusBarUtil.setStatusBarBackgroundColor
import com.topview.purejoy.common.util.StatusBarUtil.setStatusBarTextColor


/**
 * @author lanlin-code
 * 通用Activity
 */
abstract class CommonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()
        setStatusBarStyle()
        supportActionBar?.hide()
    }

    protected open fun setContentView() {
        setContentView(getLayoutId())
    }

    /**
     * 设置状态栏的背景、文字颜色等
     * */
    protected open fun setStatusBarStyle() {
        window.setStatusBarBackgroundColor(Color.TRANSPARENT)
            .setStatusBarTextColor(true)
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
     * 替换碎片，并且将事务添加到返回栈当中
     *
     * @param containerId 存放Fragment的布局ID
     * @param fragment 容器中要替换成的Fragment
     * */
    protected fun replaceAndAddToBackStack(containerId: Int, fragment: Fragment) {
        beginTransaction().replace(containerId, fragment,fragment::class.java.simpleName)
            .addToBackStack(null)
            .commit()
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
}