package com.topview.purejoy.home.tasks.login

import android.content.Context
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.topview.purejoy.common.base.ComposeFragment
import com.topview.purejoy.home.R
import com.topview.purejoy.home.components.login.PasswordLoginScreen

class PasswordFragment: ComposeFragment() {

    private val passwordViewModel by viewModels<PasswordViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        passwordViewModel.setPhone(arguments?.getString(
            resources.getString(R.string.home_label_phone_number)))
    }

    @Composable
    override fun ComposeView.FragmentContent() {
        Surface {
            val uiState by passwordViewModel.passwordLoginScreenState.observeAsState()

            if (uiState!!.loginSuccess) {
                activity?.finish()
            }

            PasswordLoginScreen(
                onLoginClick = passwordViewModel::login,
                state = uiState!!,
                onClose = {
                    activity?.finish()
                }
            )
        }
    }
}