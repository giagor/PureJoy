package com.topview.purejoy.home.tasks.login

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.topview.purejoy.common.base.ComposeFragment
import com.topview.purejoy.home.R
import com.topview.purejoy.home.components.PhoneScreen

class PhoneInputFragment: ComposeFragment() {

    private val loginViewModel: LoginViewModel by activityViewModels()

    @Composable
    override fun ComposeView.FragmentContent() {
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