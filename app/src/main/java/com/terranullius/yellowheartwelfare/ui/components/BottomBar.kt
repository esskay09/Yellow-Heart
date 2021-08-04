package com.terranullius.yellowheartwelfare.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.terranullius.yellowheartwelfare.other.Constants.AB_JOIN
import com.terranullius.yellowheartwelfare.other.Constants.AB_SHARE
import terranullius.yellowheartwelfare.R

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    colorBg: Color = MaterialTheme.colors.secondary,
    onBottomBarItemClicked: (id: String) -> Unit
) {
    BottomAppBar(modifier = modifier, cutoutShape = CircleShape, backgroundColor = colorBg, elevation = 20.dp) {
        BottomNavigation(modifier = Modifier.fillMaxWidth(), backgroundColor = colorBg) {
            BottomNavigationItem(
                icon = {
                    Icon(
                        painterResource(id = R.drawable.ic_join),
                        contentDescription = ""
                    )
                },
                onClick = {
                    onBottomBarItemClicked(AB_JOIN)
                },
                alwaysShowLabel = true,
                selected = false
            )
            BottomNavigationItem(
                icon = {
                    Icon(
                        Icons.Filled.Share,
                        contentDescription = ""
                    )
                },
                onClick = {
                    onBottomBarItemClicked(AB_SHARE)
                },
                alwaysShowLabel = true,
                selected = false
            )
        }
    }
}
