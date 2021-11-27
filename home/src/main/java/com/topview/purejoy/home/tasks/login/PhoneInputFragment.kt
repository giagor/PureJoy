package com.topview.purejoy.home.tasks.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.topview.purejoy.home.R
import com.topview.purejoy.home.components.PhoneScreen

class PhoneInputFragment: Fragment() {

    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

        setContent {
            val uiState by loginViewModel.phoneUiState.observeAsState()
            val captchaState by loginViewModel.captchaState.collectAsState()
            
            val inputManager = LocalTextInputService.current

            if (captchaState) {
                // 重置跳转状态
                loginViewModel.resetCaptchaStatus()
                val bundle = bundleOf(
                    stringResource(R.string.home_label_phone_number) to uiState?.text)
                findNavController().navigate(
                    R.id.home_nav_code,
                    bundle
                )
            }

            Surface {
                PhoneScreen(
                    uiState!!,
                    onNextStepClick = {
                        // 隐藏软键盘
                        inputManager?.hideSoftwareKeyboard()
                        loginViewModel.requestCode()
                    },
                    onClose = { activity?.finish() },
                    onTextChanged = loginViewModel::changeText
                )
            }

        }
    }
}