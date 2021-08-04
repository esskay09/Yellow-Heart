package com.terranullius.yellowheartwelfare.ui

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.pager.ExperimentalPagerApi
import com.terranullius.yellowheartwelfare.data.Initiative
import com.terranullius.yellowheartwelfare.data.ShareLinks
import com.terranullius.yellowheartwelfare.other.Constants.RT_DETAIL
import com.terranullius.yellowheartwelfare.other.Constants.RT_FEED
import com.terranullius.yellowheartwelfare.other.Constants.RT_PAYMENT_FINISH
import com.terranullius.yellowheartwelfare.other.Constants.RT_SPLASH
import com.terranullius.yellowheartwelfare.utils.Result
import com.terranullius.yellowheartwelfare.viewmodels.MainViewModel

@ExperimentalPagerApi
@Composable
fun MyApp(
    isSignedIn: Boolean,
    navController: NavHostController,
    initiatives: State<Result<List<Initiative>>>,
    onBottomBarClicked: (String) -> Unit,
    onShareDialogClicked: (link: String) -> Unit,
    onHelpClicked: (isPayable: Boolean, link: String?, amount: Int?) -> Unit,
    viewModel: MainViewModel
    ) {

    val paymentCallback = viewModel.paymentCallback.collectAsState()

    with(paymentCallback.value.getContentIfNotHandled()){
        when(this){
            is Result.Success -> {
                navController.navigate(RT_PAYMENT_FINISH)
            }
            is Result.Error -> {
                Toast.makeText(LocalContext.current, this.data.toString(), Toast.LENGTH_LONG)
            }
            else -> {

            }
        }
    }


    Surface(color = MaterialTheme.colors.primary) {

        val selectedInitiative = remember {
            mutableStateOf(
                Initiative(
                    name = "",
                    descriptions = emptyList(),
                    isPayable = true,
                    images = emptyList(),
                    order = 0,
                    shareLinks = ShareLinks("", "", "")
                )
            )
        }

        if (isSignedIn) {
            NavHost(navController = navController, startDestination = RT_SPLASH) {
                composable(RT_PAYMENT_FINISH){
                    PaymentFinishedScreen(modifier = Modifier.fillMaxSize(), navController = navController)
                }
                composable(RT_SPLASH) {
                    SplashScreen(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController
                    )
                }
                composable(RT_FEED) {
                    Feed(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        onInitiativeClicked = {
                            selectedInitiative.value = it
                            navController.navigate(RT_DETAIL)
                        },
                        initiatives = initiatives.value
                    )
                }
                composable(RT_DETAIL) {
                    InitiativeDetail(
                        modifier = Modifier.fillMaxSize(),
                        initiative = selectedInitiative.value,
                        onBottomBarItemClicked = {
                            onBottomBarClicked(it)
                        },
                        onShareDialogClicked = onShareDialogClicked,
                        onHelpClicked = { link: String?, isPayable: Boolean, amount: Int? ->
                            onHelpClicked(
                                isPayable, link, amount
                            )
                        })

                }
            }
        }
    }
}
