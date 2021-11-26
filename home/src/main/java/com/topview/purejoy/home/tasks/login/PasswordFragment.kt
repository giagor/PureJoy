package com.topview.purejoy.home.tasks.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.topview.purejoy.home.R
import com.topview.purejoy.home.components.PasswordLoginScreen

class PasswordFragment: Fragment() {

    private val passwordViewModel by viewModels<PasswordViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        passwordViewModel.setPhone(arguments?.getString(
            resources.getString(R.string.home_label_phone_number)))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(inflater.context).apply {

        setContent {
            Surface {
                val uiState by passwordViewModel.passwordLoginScreenState.observeAsState()

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
}