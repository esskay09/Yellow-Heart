package com.terranullius.yellowheartwelfare.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.paykun.sdk.eventbus.Events
import com.terranullius.yellowheartwelfare.firebase.FirebaseAuthUtils

import com.terranullius.yellowheartwelfare.other.Constants.AB_HELP
import com.terranullius.yellowheartwelfare.other.Constants.AB_JOIN
import com.terranullius.yellowheartwelfare.other.Constants.AB_SHARE
import com.terranullius.yellowheartwelfare.other.Constants.JOIN_LINK
import com.terranullius.yellowheartwelfare.other.Constants.RT_SPLASH
import com.terranullius.yellowheartwelfare.payment.PaymentUtils
import com.terranullius.yellowheartwelfare.ui.components.*
import com.terranullius.yellowheartwelfare.ui.theme.YellowHeartTheme
import com.terranullius.yellowheartwelfare.viewmodels.MainViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()

    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseAuthUtils.registerListeners(this)
        PaymentUtils.registerListeners(this)

        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current
            val isSignedIn = viewModel.isSignedInFlow.collectAsState()
            val initiatives = viewModel.initiativesFlow.collectAsState()

            navController.addOnDestinationChangedListener { navController: NavController, navDestination: NavDestination, bundle: Bundle? ->

                var window: Window? = null

                if (context is Activity) {
                    window = context.window
                    // clear FLAG_TRANSLUCENT_STATUS flag:
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    // finally change the color
                    // finally change the color
                    setStatusBarColor(R.color.secondaryColor, context)

                }

                if (window != null) {
                    when (navDestination.route) {
                        RT_SPLASH -> setStatusBarColor(R.color.secondaryLightColor, context)
                        else -> setStatusBarColor(R.color.primaryColor, context)
                    }
                }
            }

            YellowHeartTheme {
                MyApp(
                    isSignedIn = isSignedIn.value,
                    navController = navController,
                    initiatives = initiatives,
                    viewModel = viewModel,
                    onBottomBarClicked = {
                        onBottomBarClicked(it)
                    },
                    onShareDialogClicked = { link ->
                        onShareDialogClicked(link)
                    },
                    onHelpClicked = { isPayable: Boolean, link: String?, amount: Int? ->
                        onHelpDialogCLicked(isPayable, link, amount)
                    })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (FirebaseAuthUtils.isSignedIn()) {
            viewModel.onSignedIn()
        }
    }

    private fun onBottomBarClicked(id: String) {
        when (id) {
            AB_HELP -> {
            }
            AB_SHARE -> {
            }
            AB_JOIN -> {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(JOIN_LINK)
                }
                startActivity(intent)
            }
        }
    }

    private fun onHelpDialogCLicked(isPayable: Boolean, link: String?, amount: Int?) {
        if (isPayable && amount != null) {
            initiatePayment(amount)
        } else if (!isPayable && link != null) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(link)
            }
            startActivity(intent)
        }
    }

    private fun initiatePayment(amount: Int) {
        val currentUser = FirebaseAuthUtils.getUser()
        PaymentUtils.initiatePayment(this@MainActivity, currentUser, amount)
    }

    private fun onShareDialogClicked(link: String) {
        if (link.isNotBlank()) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(link)
            }
            startActivity(intent)
        }
    }

    private fun setStatusBarColor(@ColorRes colorRes: Int, context: Context) {
        window.statusBarColor = ContextCompat.getColor(
            context, colorRes
        )
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun getResults(message: Events.PaymentMessage) {
        viewModel.onPaymentFinished(message)
    }
}
