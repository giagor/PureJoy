package com.topview.purejoy.home.components.about

import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.topview.purejoy.common.app.CommonApplication
import com.topview.purejoy.common.util.createImageRequestForCoil
import com.topview.purejoy.home.R
import com.topview.purejoy.home.components.login.ScreenTitle

@Composable
internal fun AboutScreen(
    onBackClick: () -> Unit = {},
    onSourceClick: () -> Unit = {},
    onLicensesClick: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            AboutScreenTitle(
                title = stringResource(id = R.string.home_about_title),
                onBackClick = onBackClick
            )
        }
    ) {
        Column(
            modifier = Modifier.verticalScroll(state = rememberScrollState())
        ) {
            AppData()
            AboutItem(
                content = stringResource(id = R.string.home_about_code_source),
                onClick = onSourceClick
            )
            AboutItem(
                content = stringResource(id = R.string.home_about_licenses),
                onClick = onLicensesClick
            )
        }
    }
}

@Composable
internal fun AboutScreenTitle(
    modifier: Modifier = Modifier,
    title: String,
    onBackClick: () -> Unit = {}
) {
    ScreenTitle(
        modifier = modifier,
        title = title,
        leadingContent = {
            Icon(
                painter = painterResource(id = R.drawable.home_ic_arrow_back_24),
                contentDescription = null,
                modifier = Modifier
                    .padding(10.dp)
                    .clickable(onClick = onBackClick)
            )
        }
    )
}

@Composable
private fun AppData(
    modifier: Modifier = Modifier
) {
    Surface(
        elevation = 5.dp,
        modifier = modifier
            .padding(bottom = 5.dp)
            .height(220.dp)
            .fillMaxWidth()
    ) {
        ConstraintLayout {
            val (icon, name, version, spacer) = createRefs()
            Image(
                painter = rememberAsyncImagePainter(
                    model = createImageRequestForCoil(
                        data = R.mipmap.ic_launcher
                    )
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .constrainAs(icon) {
                        bottom.linkTo(spacer.top)
                        centerHorizontallyTo(parent)
                    },
            )
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .constrainAs(spacer) {
                    centerTo(parent)
                }
            )
            Text(
                text = getAppName(),
                modifier = Modifier
                    .constrainAs(name) {
                        centerHorizontallyTo(parent)
                        top.linkTo(spacer.bottom, 10.dp)
                    },
                style = TextStyle.Default.copy(
                    fontSize = 21.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = getAppVersion(),
                modifier = Modifier
                    .constrainAs(version) {
                        centerHorizontallyTo(parent)
                        top.linkTo(name.bottom, 10.dp)
                    },
                style = TextStyle.Default.copy(
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                )
            )
        }
    }
}

@Composable
private fun AboutItem(
    content: String,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple()
            )
    ) {
        Text(
            text = content,
            fontSize = 17.sp,
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 20.dp)
        )
    }
}

private fun getAppName(): String =
    CommonApplication.getContext().run {
        resources.getString(applicationInfo.labelRes)
    }

private fun getAppVersion(): String {
    val context = CommonApplication.getContext()
    val packageManager = context.packageManager as PackageManager
    return packageManager.getPackageInfo(context.packageName, 0).run {
        "Version $versionName"
    }
}


@Preview(showBackground = true)
@Composable
private fun AboutScreenPreview() {

}