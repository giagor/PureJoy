package com.topview.purejoy.home

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.topview.purejoy.common.base.binding.BindingActivity
import com.topview.purejoy.home.databinding.HomeActivityHomeBinding
import com.topview.purejoy.home.discover.HomeDiscoverFragment

private const val TAG = "HomeActivity"

class HomeActivity : BindingActivity<HomeActivityHomeBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.homeActivity = this
        addFragment(R.id.home_fl_fragment_layout, HomeDiscoverFragment())
    }

    override fun getLayoutId(): Int {
        return R.layout.home_activity_home
    }

    /**
     * BottomNavigationView选择时的监听
     * */
    fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_bottom_navi_discover -> {
                Log.d(TAG, "initEvent: discover")
                true
            }

            R.id.menu_bottom_navi_mine -> {
                Log.d(TAG, "initEvent: mine")
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