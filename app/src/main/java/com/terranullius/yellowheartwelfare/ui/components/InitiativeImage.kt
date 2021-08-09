package com.terranullius.yellowheartwelfare.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.ImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun InitiativeImage(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    page: Int,
    imageSize: Float,
    painter: ImagePainter
)
    {
        Box(modifier = modifier) {
            when (painter.state) {
                is ImagePainter.State.Loading -> {
                    Surface(color = Color.Gray.copy(alpha = 0.3f)) {
                        LoadingComposable(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(x = 0.dp, y = (-30).dp)
                        )
                    }
                }
                is ImagePainter.State.Success -> {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                if (pagerState.targetPage == page || pagerState.currentPage == page) {
                                    scaleY = imageSize
                                    scaleX = imageSize
                                } else {
                                    scaleY = 1f
                                    scaleX = 1f
                                }
                            },
                        painter = painter,
                        alignment = Alignment.TopStart,
                        contentScale = ContentScale.FillBounds,
                        contentDescription = ""
                    )
                }
                is ImagePainter.State.Error -> {
                    ErrorComposable(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = 0.dp, y = (-30).dp)
                    )
                }
                ImagePainter.State.Empty -> Unit

            }
        }
        }
