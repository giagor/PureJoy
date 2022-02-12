package com.topview.purejoy.home.tasks.login

import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.topview.purejoy.common.base.ComposeFragment
import com.topview.purejoy.home.R
import com.topview.purejoy.home.components.login.CaptchaScreen
import com.topview.purejoy.home.components.login.rememberCaptchaScreenState
import com.topview.purejoy.home.components.status.PageState

class CaptchaFragment: ComposeFragment() {

    private val viewModel: CaptchaViewModel by viewModels()
    private val loginViewModel: LoginViewModel by activityViewModels()

    private lateinit var phone: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        phone = arguments?.getString(resources.getString(R.string.home_label_phone_number))
            ?: error("phone number is null")
    }

    @Composable
    override fun ComposeView.FragmentContent() {
        val phoneArgName = stringResource(R.string.home_label_phone_number)
        val uiState = rememberCaptchaScreenState(phone = phone)
        val loadState by viewModel.pageState.collectAsState()

        LaunchedEffect(loadState) {
            when (loadState) {
                is PageState.Success -> {
                    activity?.finish()
                }
                else -> {}
            }
        }

        LaunchedEffect(Unit) {
            viewModel.snackBarEvent.collect {
                uiState.snackBarHostState.showSnackbar(it.message)
            }
        }

        Surface {
            CaptchaScreen(
                time = viewModel.time,
                state = uiState,
                onResendClick = {
                    loginViewModel.requestCode(uiState.phone)
                    viewModel.restartCountDown()
                },
                onChangePhoneClick = {
                    findNavController().popBackStack()
                },
                onPasswordClick = {
                    val bundle = bundleOf(phoneArgName to uiState.phone)
                    findNavController().navigate(
                        R.id.home_nav_password,
                        bundle
                    )
                },
                onCaptchaFull = {
                    val captcha = uiState.captchaFieldState.text
                    // 清空验证码
                    uiState.captchaFieldState.text = ""
                    viewModel.loginOrRegister(
                        phone = uiState.phone,
                        captcha = captcha
                    )
                },
                onClose = {
                    activity?.finish()
                }
            )
            CrossFadeLoginIndicator(
                loadState = loadState,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun CrossFadeLoginIndicator(
    modifier: Modifier = Modifier,
    loadState: PageState,
) {
    Crossfade(loadState) {
        when (it) {
            is PageState.Loading -> {
                Box(
                    modifier = modifier,
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.Gray
                    )
                }
            }
            else -> {}
        }
    }
}