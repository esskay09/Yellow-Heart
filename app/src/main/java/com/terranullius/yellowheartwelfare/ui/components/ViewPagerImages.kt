package com.terranullius.yellowheartwelfare.ui.components

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalCoilApi::class)
@ExperimentalPagerApi
@Composable
fun ViewPagerImages(
    modifier: Modifier = Modifier,
    images: List<String>,
    pagerState: PagerState,
    onYoutubePlayerClicked: () -> Unit = {},
    isVideoPlaying: MutableState<Boolean>
) {

    val youtubePlayerList = remember {
        mutableSetOf<YoutubePlayerIndexed>()
    }

    var currentPlayingVideoIndex by remember {
        mutableStateOf(15)
    }

    var imageSize by remember {
        mutableStateOf(1f)
    }

    LaunchedEffect(key1 = pagerState.currentPage) {
        launch {
            while (imageSize <= 1.4f) {

                if (!pagerState.isScrollInProgress) imageSize += 0.0013f
                else imageSize = 1f

                if (imageSize > 1.4f) imageSize = 1f

                delay(50L)
            }
        }
        val currentPage = pagerState.currentPage
        val currentYoutubePlayer = youtubePlayerList.find {
            it.index == currentPage
        }
        //Check if it's an image
        if (!images[currentPage].contains("youtubeID=")) {
            //Image
            scrollPage(pagerState)
            youtubePlayerList.forEach {
                it.pause()
            }
        } else { //Video
            currentYoutubePlayer?.let { currentPlayer ->
                if (isVideoPlaying.value) {
                    if (currentPlayingVideoIndex != currentPage) {
                        onBuffered(currentPlayer) {
                            if (isVideoPlaying.value) {
                                if (currentPlayingVideoIndex != currentPage) scrollPage(pagerState)
                            } else currentPlayer.play()
                        }
                    } else onBuffered(currentPlayer) {
                        if (isVideoPlaying.value) {
                            if (currentPlayingVideoIndex != currentPage) scrollPage(pagerState)
                        } else currentPlayer.play()
                    }
                } else {
                    onBuffered(currentPlayer) {
                        if (isVideoPlaying.value) {
                            if (currentPlayingVideoIndex != currentPage) scrollPage(pagerState)
                        } else currentPlayer.play()
                    }
                }
            }
        }
    }

    HorizontalPager(state = pagerState, modifier = modifier, reverseLayout = false) { page ->

        Box(modifier = Modifier.fillMaxSize()) {

            val contextCurrent = LocalContext.current

            val youTubePlayerView: YouTubePlayerView = remember {
                YouTubePlayerView(contextCurrent)
            }

            DisposableEffect(key1 = null) {
                onDispose {
                    youTubePlayerView.release()
                }
            }

            if (images[page].contains("youtubeID")) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize(),
                    factory = { context ->
                        (context as AppCompatActivity).lifecycle.addObserver(youTubePlayerView)
                        val videoId = images[page].substringAfter("=")
                        youTubePlayerView.apply {
                            getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                                override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                                    youTubePlayer.cueVideo(videoId, 0f)
                                    youtubePlayerList.find {
                                        it.index == page
                                    }?.let {
                                        it.youTubePlayer = youTubePlayer
                                    } ?: youtubePlayerList.add(
                                        YoutubePlayerIndexed(
                                            youTubePlayer,
                                            page
                                        )
                                    )
                                }
                            })
                            addYouTubePlayerListener(object : YouTubePlayerListener {
                                override fun onApiChange(youTubePlayer: YouTubePlayer) {

                                }

                                override fun onCurrentSecond(
                                    youTubePlayer: YouTubePlayer,
                                    second: Float
                                ) {

                                }

                                override fun onError(
                                    youTubePlayer: YouTubePlayer,
                                    error: PlayerConstants.PlayerError
                                ) {
                                    setPlaying(false)
                                }

                                private fun setPlaying(value: Boolean) {
                                    if (currentPlayingVideoIndex == page) {
                                        isVideoPlaying.value = value
                                    }
                                }

                                override fun onPlaybackQualityChange(
                                    youTubePlayer: YouTubePlayer,
                                    playbackQuality: PlayerConstants.PlaybackQuality
                                ) {

                                }

                                override fun onPlaybackRateChange(
                                    youTubePlayer: YouTubePlayer,
                                    playbackRate: PlayerConstants.PlaybackRate
                                ) {

                                }

                                override fun onReady(youTubePlayer: YouTubePlayer) {

                                }

                                override fun onStateChange(
                                    youTubePlayer: YouTubePlayer,
                                    state: PlayerState
                                ) {

                                    val currentPlayer = youtubePlayerList.find { playerIndexed ->
                                        playerIndexed.index == page
                                    }

                                    when (state) {
                                        PlayerState.PAUSED -> {
                                            currentPlayingVideoIndex = 15
                                            currentPlayer?.isBuffering = false
                                        }
                                        PlayerState.ENDED -> {
                                            setPlaying(false)
                                            currentPlayer?.isBuffering = false
                                        }
                                        PlayerState.PLAYING -> {
                                            currentPlayingVideoIndex = page
                                            isVideoPlaying.value = true
                                            currentPlayer?.isBuffering = false
                                        }
                                        PlayerState.BUFFERING -> currentPlayer?.isBuffering = true
                                        PlayerState.VIDEO_CUED -> currentPlayer?.isBuffering = false
                                        PlayerState.UNKNOWN -> currentPlayer?.isBuffering = false
                                        else -> {
                                        }
                                    }
                                }

                                override fun onVideoDuration(
                                    youTubePlayer: YouTubePlayer,
                                    duration: Float
                                ) {

                                }

                                override fun onVideoId(
                                    youTubePlayer: YouTubePlayer,
                                    videoId: String
                                ) {

                                }

                                override fun onVideoLoadedFraction(
                                    youTubePlayer: YouTubePlayer,
                                    loadedFraction: Float
                                ) {

                                }
                            })
                        }
                        youTubePlayerView
                    }) {
                    it.apply {
                        enterFullScreen()
                        enableBackgroundPlayback(false)
                        getPlayerUiController().apply {
                            showFullscreenButton(false)
                            showVideoTitle(false)
                            showYouTubeButton(false)
                            showSeekBar(true)
                            showMenuButton(false)
                        }
                    }
                }

                Box(modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxHeight(0.89f)
                    .fillMaxWidth(0.4f)
                    .clickable {
                        onYoutubePlayerClicked()
                    })
                Box(modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight(0.89f)
                    .fillMaxWidth(0.4f)
                    .clickable {
                        onYoutubePlayerClicked()
                    })
                Box(modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
                    .clickable {
                        onYoutubePlayerClicked()
                    })
                Box(modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
                    .clickable {
                        onYoutubePlayerClicked()
                    })

            } else {
                val painter = rememberImagePainter(data = images[page], builder = {
                    crossfade(true)
                    scale(Scale.FILL)
                })

                InitiativeImage(
                    modifier = Modifier.fillMaxSize(),
                    pagerState,
                    page,
                    imageSize,
                    painter
                )
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
fun CoroutineScope.scrollPage(pagerState: PagerState) {
    Log.d(
        "fuck",
        "pageCount: ${pagerState.pageCount}  currentPage: ${(pagerState.currentPage + 1) % (pagerState.pageCount)}"
    )
    launch {
        val randomDelayMillis = Random.nextInt(4, 11) * 1000L
        delay(randomDelayMillis)
        pagerState.animateScrollToPage((pagerState.currentPage + 1) % (pagerState.pageCount))
    }
}

private fun CoroutineScope.onBuffered(youTubePlayer: YoutubePlayerIndexed, onBuffered: () -> Unit) {
    if (!youTubePlayer.isBuffering) onBuffered()
    else {
        launch {
            while (true) {
                if (!youTubePlayer.isBuffering) {
                    onBuffered()
                    return@launch
                } else delay(150L)
            }
        }
    }
}

class YoutubePlayerIndexed(
    var youTubePlayer: YouTubePlayer,
    val index: Int,
    var isBuffering: Boolean = false
) {
    fun play() {
        youTubePlayer.play()
    }

    fun pause() {
        youTubePlayer.pause()
    }
}