package com.verdenroz.fiveshades.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.verdenroz.fiveshades.ui.home.HomeRoute

@Composable
fun FVSNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeRoute()
        }


    }
}