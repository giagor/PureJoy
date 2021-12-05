package com.topview.purejoy.home.tasks.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.topview.purejoy.home.R
import com.topview.purejoy.home.components.CaptchaScreen

class CaptchaFragment: Fragment() {

    private val viewModel: CaptchaViewModel by viewModels()

    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val phone = arguments?.getString(resources.getString(R.string.home_label_phone_number))
        viewModel.setPhone(phone)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        setContent {
            val phoneArgName = stringResource(R.string.home_label_phone_number)
            val uiState by viewModel.captchaUiState.observeAsState()

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


}