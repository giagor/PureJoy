package com.topview.purejoy.home.tasks.login

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.accompanist.insets.ProvideWindowInsets
import com.topview.purejoy.common.base.ComposeActivity
import com.topview.purejoy.home.databinding.ActivityHomeLoginBinding

class LoginActivity: ComposeActivity() {
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