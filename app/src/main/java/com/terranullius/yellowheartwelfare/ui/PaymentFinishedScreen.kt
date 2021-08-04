package com.terranullius.yellowheartwelfare.ui

import android.animation.Animator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.airbnb.lottie.LottieAnimationView
import terranullius.yellowheartwelfare.R

@Composable
fun PaymentFinishedScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    Box(modifier = modifier) {
        var showText by remember {
            mutableStateOf(false)
        }
        Column(modifier = Modifier.align(Alignment.Center)) {
            Surface(modifier = modifier, color = MaterialTheme.colors.secondaryVariant) {
                val context = LocalContext.current
                val customView = remember { LottieAnimationView(context) }

                customView.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        showText = true
                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationRepeat(animation: Animator?) {

                    }
                })

                AndroidView({ customView }, modifier = modifier) { view ->
                    with(view) {
                        setAnimation(R.raw.heart_splash)
                        repeatCount = 0
                        playAnimation()
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (showText) InitiativeTitle(
                    text = "Thank You!!",
                    size = MaterialTheme.typography.h5
                )
            }
        }
    }
}