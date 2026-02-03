package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import com.example.todolist.navigation.TodoNavHost
import com.example.todolist.ui.feature.pages.AuthViewModel
import com.example.todolist.ui.theme.ToDoListTheme

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Box (
                modifier = Modifier.safeDrawingPadding()
            ){
                ToDoListTheme {
                    TodoNavHost(authViewModel = authViewModel)
                }
            }
        }
    }
}


