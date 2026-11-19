package com.sakusisswa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.sakusisswa.ui.navigation.SakuSiswaNavGraph
import com.sakusisswa.ui.theme.SakuSiswaTheme
import com.sakusisswa.viewmodel.TransactionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SakuSiswaTheme {
                val navController = rememberNavController()
                val viewModel: TransactionViewModel = viewModel()

                SakuSiswaNavGraph(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}
