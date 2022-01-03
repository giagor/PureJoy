package com.topview.purejoy.home

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.topview.purejoy.common.base.binding.BindingActivity
import com.topview.purejoy.home.databinding.ActivityHomeHomeBinding
import com.topview.purejoy.home.discover.HomeDiscoverFragment
import com.topview.purejoy.home.mine.HomeMineFragment
import com.topview.purejoy.home.router.HomeRouter

private const val TAG = "HomeActivity"

class HomeActivity : BindingActivity<ActivityHomeHomeBinding>() {

    private var discoverFragment: Fragment? = null
    private var mineFragment: Fragment? = null
    private var videoFragment: Fragment? = null
    private var curShowFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.homeActivity = this
        initView()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_home_home
    }

    private fun initView() {
        discoverFragment = HomeDiscoverFragment()
        curShowFragment = discoverFragment
        addFragment(R.id.home_fl_fragment_layout, curShowFragment!!)
    }

    /**
     * BottomNavigationView选择时的监听
     * */
    fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_bottom_navi_discover -> {
                Log.d(TAG, "initEvent: discover")
                hide(curShowFragment!!)
                if (discoverFragment == null) {
                    discoverFragment = HomeRouter.routeToDiscoverFragment()
                    addFragment(R.id.home_fl_fragment_layout, discoverFragment!!)
                } else {
                    show(discoverFragment!!)
                }
                curShowFragment = discoverFragment
                true
            }

            R.id.menu_bottom_navi_mine -> {
                Log.d(TAG, "initEvent: mine")
                hide(curShowFragment!!)
                if (mineFragment == null) {
                    mineFragment = HomeMineFragment()
                    addFragment(R.id.home_fl_fragment_layout, mineFragment!!)
                } else {
                    show(mineFragment!!)
                }
                curShowFragment = mineFragment
                true
            }

            R.id.menu_bottom_navi_video -> {
                Log.d(TAG, "initEvent: video")
                true
            }

            else -> {
                Log.d(TAG, "initEvent: others")
                false
            }
        }
    }

    /**
     * BottomNavigationView重选择时的监听
     * */
    fun onNavigationItemReselected(item: MenuItem) {

    }
}