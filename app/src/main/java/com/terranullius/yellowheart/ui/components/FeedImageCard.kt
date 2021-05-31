package com.terranullius.yellowheart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.terranullius.yellowheart.data.Initiative

@ExperimentalPagerApi
@Composable
fun FeedImageCard(
    initiative: Initiative,
    modifier: Modifier = Modifier,
    onInitiativeClicked: (initiative: Initiative) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = initiative.images.size, initialOffscreenLimit = 2)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onInitiativeClicked(initiative.copy(initialPage = pagerState.currentPage))
            },
        shape = RoundedCornerShape(15.dp),
        elevation = 5.dp
    ) {
        Box(modifier = Modifier.height(270.dp)) {
            ViewPagerImages(
                pagerState = pagerState,
                modifier = Modifier.fillMaxSize(),
                images = initiative.images
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black
                            ),
                            startY = 300f
                        )
                    )
            )
            Text(
                text = initiative.name, modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                style = MaterialTheme.typography.h6.copy(
                    color = Color.White,
                    fontFamily = FontFamily.Cursive
                )
            )
            if (false) HelpButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                onClick = { }
            )
        }
    }
}

