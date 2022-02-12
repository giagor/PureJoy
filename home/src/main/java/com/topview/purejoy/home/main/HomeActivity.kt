package com.topview.purejoy.home.main

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.topview.purejoy.common.music.data.Wrapper
import com.topview.purejoy.common.music.view.bottom.MusicBottomView
import com.topview.purejoy.common.mvvm.activity.MVVMActivity
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.ActivityHomeHomeBinding
import com.topview.purejoy.home.discover.HomeDiscoverFragment
import com.topview.purejoy.home.router.HomeRouter
import com.topview.purejoy.home.util.getAndroidViewModelFactory

private const val TAG = "HomeActivity"

class HomeActivity : MVVMActivity<HomeViewModel, ActivityHomeHomeBinding>(),
    HomeDiscoverFragment.RecommendNewSongPlayListener {

    private val bottomMusicBar: MusicBottomView by lazy {
        MusicBottomView(activity = this)
    }

    private var discoverFragment: Fragment? = null
    private var mineFragment: Fragment? = null
    private var videoFragment: Fragment? = null
    private var curShowFragment: Fragment? = null
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.homeActivity = this
        initView()
        initData()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_home_home
    }

    private fun initView() {
        discoverFragment = HomeDiscoverFragment().apply {
            setRecommendNewSongPlayListener(this@HomeActivity)
        }
        curShowFragment = discoverFragment
        addFragment(R.id.home_fl_fragment_layout, curShowFragment!!)

        bottomNavigationView = binding.homeBnvBottomNavigation
        bottomNavigationView.post {
            bottomMusicBar.addMusicBottomBar(bottomNavigationView.height)
        }
    }

    private fun initData() {
        viewModel.keepLogin()
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
                    // 设置监听器
                    (discoverFragment as? HomeDiscoverFragment)?.setRecommendNewSongPlayListener(
                        this
                    )
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
                    mineFragment = HomeRouter.routeToMineFragment()
                    addFragment(R.id.home_fl_fragment_layout, mineFragment!!)
                } else {
                    show(mineFragment!!)
                }
                curShowFragment = mineFragment
                true
            }

            R.id.menu_bottom_navi_video -> {
                Log.d(TAG, "initEvent: video")
                hide(curShowFragment!!)
                if (videoFragment == null) {
                    videoFragment = HomeRouter.routeToVideoFragment()
                    addFragment(R.id.home_fl_fragment_layout, videoFragment!!)
                } else {
                    show(videoFragment!!)
                }
                curShowFragment = videoFragment
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

    override fun getViewModelClass(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun createFactory(): ViewModelProvider.Factory = getAndroidViewModelFactory()

    override fun onRecommendNewSongClick(position: Int, list: List<Wrapper>) {
        bottomMusicBar.controller.dataController?.clear()
        bottomMusicBar.controller.dataController?.addAll(list)
        bottomMusicBar.controller.playerController?.jumpTo(position)
    }
}