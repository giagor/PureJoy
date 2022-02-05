package com.topview.purejoy.home.components.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.topview.purejoy.home.R

@Composable
internal fun LicensesScreen(
    onBackClick: () -> Unit = {},
    onItemClick: (Library) -> Unit = {}
) {
    Scaffold(
        topBar = {
            AboutScreenTitle(
                title = stringResource(id = R.string.home_about_licenses),
                onBackClick = onBackClick
            )
        }
    ) {
        val context = LocalContext.current
        val libraries = Libs(context).libraries.sortedWith(compareBy(
            String.CASE_INSENSITIVE_ORDER) { it.libraryName })
        LazyColumn {
            items(libraries) {
                LicensesItem(
                    library = it,
                    modifier = Modifier
                        .padding(vertical = 12.dp, horizontal = 8.dp)
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(),
                            onClick = { onItemClick(it) }
                        )
                )
            }
        }
    }
}

@Composable
private fun LicensesItem(
    modifier: Modifier = Modifier,
    library: Library,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "${library.libraryName} ${library.libraryVersion}",
            fontWeight = FontWeight.Bold
        )
        Text(
            text = library.libraryArtifactId
        )
        library.licenses?.let { set ->
            Text(
                text = set.joinToString { it.licenseName },
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        }
    }
}