package com.topview.purejoy.home.tasks.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alibaba.android.arouter.facade.annotation.Route
import com.topview.purejoy.common.base.ComposeActivity
import com.topview.purejoy.home.components.about.AboutScreen
import com.topview.purejoy.home.components.about.LicensesScreen
import com.topview.purejoy.home.router.HomeRouter

@Route(path = HomeRouter.ACTIVITY_HOME_ABOUT)
class AboutActivity: ComposeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = ABOUT_SCREEN
            ) {
                composable(ABOUT_SCREEN) {
                    AboutScreen(
                        onBackClick = {
                            onBackPressed()
                        },
                        onSourceClick = {
                            startActivityByUrl("https://github.com/giagor/PureJoy")
                        },
                        onLicensesClick = {
                            navController.navigate(LICENSES_SCREEN)
                        },
                    )
                }
                composable(LICENSES_SCREEN) {
                    LicensesScreen(
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onItemClick = {
                            startActivityByUrl(it.libraryWebsite)
                        }
                    )
                }
            }
        }
    }

    companion object {
        const val ABOUT_SCREEN = "about"
        const val LICENSES_SCREEN = "licenses"
    }

    private fun startActivityByUrl(url: String) {
        if (url.startsWith(prefix = "http", ignoreCase = true)) {
            Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }.also { startActivity(it) }
        }
    }
}