package com.terranullius.yellowheart.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.pager.ExperimentalPagerApi
import com.terranullius.yellowheart.data.Initiative
import com.terranullius.yellowheart.data.ShareLinks
import com.terranullius.yellowheart.other.Constants.RT_DETAIL
import com.terranullius.yellowheart.other.Constants.RT_FEED
import com.terranullius.yellowheart.other.Constants.RT_SPLASH
import com.terranullius.yellowheart.utils.PaymentCallback
import com.terranullius.yellowheart.utils.Result
import com.terranullius.yellowheart.viewmodels.MainViewModel

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

    val paymentCallack = viewModel.paymentCallback.collectAsState()

    when(paymentCallack.value.getContentIfNotHandled()){
        is PaymentCallback.success -> //TODO
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
