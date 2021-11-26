package com.topview.purejoy.home.tasks.login

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.accompanist.insets.ProvideWindowInsets
import com.topview.purejoy.common.base.CommonActivity
import com.topview.purejoy.home.databinding.HomeActivityLoginBinding

class LoginActivity: CommonActivity() {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProvideWindowInsets(false) {
                AndroidViewBinding(HomeActivityLoginBinding::inflate)
            }
        }
    }

    override fun getLayoutId() = 0

    override fun setContentView() {}
}