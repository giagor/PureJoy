package com.topview.purejoy.home

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.topview.purejoy.common.base.binding.BindingActivity
import com.topview.purejoy.common.music.view.bottom.MusicBottomView
import com.topview.purejoy.common.util.UserManager
import com.topview.purejoy.common.util.UserManager.userLiveData
import com.topview.purejoy.home.data.repo.LoginRepository
import com.topview.purejoy.home.databinding.ActivityHomeHomeBinding
import com.topview.purejoy.home.discover.HomeDiscoverFragment
import com.topview.purejoy.home.router.HomeRouter

private const val TAG = "HomeActivity"

class HomeActivity : BindingActivity<ActivityHomeHomeBinding>(){

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
        getUserData()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_home_home
    }

    private fun initView() {
        discoverFragment = HomeDiscoverFragment()
        curShowFragment = discoverFragment
        addFragment(R.id.home_fl_fragment_layout, curShowFragment!!)
        
        bottomNavigationView = binding.homeBnvBottomNavigation
        bottomNavigationView.post {
            bottomMusicBar.addMusicBottomBar(bottomNavigationView.height)
        }
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

    // TODO 重构到某个ViewModel内
    // 调用/login/status确定登录状态，并加载用户信息
    // 这个方法应当尽早调用而不是等待某个点击事件
    private fun getUserData() {
        lifecycleScope.launchWhenResumed {
            if (userLiveData.value == null) {
                LoginRepository.checkLoginStatus()?.let {
                    UserManager.login(it)
                }
            }
        }
    }
}