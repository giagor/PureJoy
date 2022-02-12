package com.topview.purejoy.home.tasks.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.topview.purejoy.common.base.ComposeFragment
import com.topview.purejoy.common.util.UserManager
import com.topview.purejoy.home.components.video.HomeVideoScreen
import com.topview.purejoy.home.components.video.LoginRoutePage
import com.topview.purejoy.home.router.HomeRouter
import com.topview.purejoy.home.util.ProvideMusicController

@Route(path = HomeRouter.FRAGMENT_HOME_VIDEO)
class HomeVideoFragment: ComposeFragment() {

    private val videoViewModel: HomeVideoViewModel by viewModels()

    @Composable
    override fun ComposeView.FragmentContent() {
        val user by UserManager.userLiveData.observeAsState()
        ProvideMusicController {
            if (user == null) {
                LoginRoutePage()
            } else {
                HomeVideoScreen(viewModel = videoViewModel)
            }
        }
    }
}