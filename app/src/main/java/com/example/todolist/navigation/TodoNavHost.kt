package com.example.todolist.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.todolist.ui.feature.addedit.AddEditScreen
import com.example.todolist.ui.feature.list.ListScreen
import com.example.todolist.ui.feature.pages.AuthViewModel
import com.example.todolist.ui.feature.pages.LoginPage
import com.example.todolist.ui.feature.pages.SingupPage
import kotlinx.serialization.Serializable

@Serializable
object ListRoute

@Serializable
data class AddEditRoute(val id: Long? = null)

@Serializable
object LoginRoute

@Serializable
object SignupRoute

@Composable
fun TodoNavHost(modifier: Modifier = Modifier, authViewModel : AuthViewModel) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = LoginRoute)  {
        composable<ListRoute>{
            ListScreen(
                navigateToAddEditScreen = {id ->
                    navController.navigate(AddEditRoute(id = id))
                }
            )
        }

        composable<AddEditRoute> { backStackEntry ->
            val addEditRoute = backStackEntry.toRoute<AddEditRoute>()
            AddEditScreen(
                id = addEditRoute.id,
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<LoginRoute>{
            LoginPage(modifier, navController, authViewModel)
        }

        composable<SignupRoute>{
            SingupPage(modifier, navController, authViewModel)
        }
    }
}