package com.topview.purejoy.home

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.topview.purejoy.common.base.CommonActivity
import com.topview.purejoy.home.discover.HomeDiscoverFragment

private const val TAG = "HomeActivity"

class HomeActivity : CommonActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initEvent()
        addFragment(R.id.home_fl_fragment_layout, HomeDiscoverFragment())
    }

    override fun getLayoutId(): Int {
        return R.layout.home_activity_home
    }

    private fun initView() {
        bottomNavigation = findViewById(R.id.home_bnv_bottom_navigation)
    }

    private fun initEvent() {
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
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

        bottomNavigation.setOnNavigationItemReselectedListener {}
    }

}