package com.topview.purejoy.home.tasks.login

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.accompanist.insets.ProvideWindowInsets
import com.topview.purejoy.common.base.ComposeActivity
import com.topview.purejoy.home.databinding.ActivityHomeLoginBinding
import com.topview.purejoy.home.router.HomeRouter

@Route(path = HomeRouter.ACTIVITY_HOME_LOGIN)
class LoginActivity : ComposeActivity() {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProvideWindowInsets(false) {
                AndroidViewBinding(ActivityHomeLoginBinding::inflate)
            }
        }
    }
}