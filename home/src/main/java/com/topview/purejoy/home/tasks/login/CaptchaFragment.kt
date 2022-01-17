package com.topview.purejoy.home.tasks.login

import android.content.Context
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.topview.purejoy.common.base.ComposeFragment
import com.topview.purejoy.home.R
import com.topview.purejoy.home.components.CaptchaScreen

class CaptchaFragment: ComposeFragment() {

    private val viewModel: CaptchaViewModel by viewModels()

    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val phone = arguments?.getString(resources.getString(R.string.home_label_phone_number))
        viewModel.setPhone(phone)
    }

    @Composable
    override fun ComposeView.FragmentContent() {
        val phoneArgName = stringResource(R.string.home_label_phone_number)
        val uiState by viewModel.captchaUiState.observeAsState()

        if (uiState!!.loginSuccess) {
            activity?.finish()
        }

        Surface {
            CaptchaScreen(
                time = viewModel.time,
                state = uiState!!,
                onResendClick = {
                    loginViewModel.requestCode(uiState?.phone)
                    viewModel.restartCountDown()
                },
                onChangePhoneClick = {
                    findNavController().popBackStack()
                },
                onPasswordClick = {
                    val bundle = bundleOf(phoneArgName to uiState?.phone)
                    findNavController().navigate(
                        R.id.home_nav_password,
                        bundle
                    )
                },
                onCaptchaFull = {
                    viewModel.loginOrRegister()
                },
                onClose = {
                    activity?.finish()
                }
            )
        }
    }
}