package com.topview.purejoy.home.tasks.login

import android.content.Context
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.topview.purejoy.common.base.ComposeFragment
import com.topview.purejoy.home.R
import com.topview.purejoy.home.components.login.PasswordLoginScreen
import com.topview.purejoy.home.components.status.PageState

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
            val uiState by passwordViewModel.screenState.collectAsState()
            val snackBarHostState = remember {
                SnackbarHostState()
            }
            LaunchedEffect(uiState.pageState) {
                when(uiState.pageState) {
                    is PageState.Success -> {
                        activity?.finish()
                    }
                    else -> {}
                }
            }

            LaunchedEffect(Unit) {
                passwordViewModel.snackBarEvent.collect {
                    snackBarHostState.showSnackbar(it.message)
                }
            }

            PasswordLoginScreen(
                onLoginClick = passwordViewModel::login,
                state = uiState,
                snackBarHostState = snackBarHostState,
                onClose = {
                    activity?.finish()
                }
            )
        }
    }
}