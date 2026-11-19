package com.sakusisswa.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sakusisswa.ui.screen.AddTransactionScreen
import com.sakusisswa.ui.screen.EwalletConnectScreen
import com.sakusisswa.ui.screen.HistoryScreen
import com.sakusisswa.ui.screen.HomeScreen
import com.sakusisswa.ui.screen.SettingsScreen
import com.sakusisswa.ui.screen.StatisticScreen
import com.sakusisswa.viewmodel.TransactionViewModel

/**
 * Rute navigasi aplikasi SakuSiswa.
 */
object Routes {
    const val HOME = "home"
    const val ADD_TRANSACTION = "add_transaction"
    const val HISTORY = "history"
    const val STATISTIC = "statistic"
    const val EWALLET = "ewallet"
    const val SETTINGS = "settings"
}

@Composable
fun SakuSiswaNavGraph(
    navController: NavHostController,
    viewModel: TransactionViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToAdd = { navController.navigate(Routes.ADD_TRANSACTION) },
                onNavigateToHistory = { navController.navigate(Routes.HISTORY) },
                onNavigateToStatistic = { navController.navigate(Routes.STATISTIC) },
                onNavigateToEwallet = { navController.navigate(Routes.EWALLET) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.ADD_TRANSACTION) {
            AddTransactionScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.STATISTIC) {
            StatisticScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.EWALLET) {
            EwalletConnectScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
