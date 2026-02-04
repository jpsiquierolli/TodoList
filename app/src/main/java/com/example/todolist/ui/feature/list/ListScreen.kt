package com.example.todolist.ui.feature.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.todolist.data.FirebaseTodoRepository
import com.example.todolist.domain.todo1
import com.example.todolist.domain.todo2
import com.example.todolist.domain.todo3
import com.example.todolist.navigation.AddEditRoute
import com.example.todolist.navigation.LoginRoute
import com.example.todolist.ui.UiEvent
import com.example.todolist.ui.components.TodoItem
import com.example.todolist.ui.feature.pages.AuthState
import com.example.todolist.ui.feature.pages.AuthViewModel
import com.example.todolist.ui.theme.ToDoListTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    navigateToAddEditScreen: (id: String?) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState = authViewModel.authState.observeAsState()
    val repository = FirebaseTodoRepository()

    val viewModel = viewModel<ListViewModel>() {
        ListViewModel(repository = repository)
    }
    val todos by viewModel.todos.collectAsState()

    // Removed, on android, using the backgesture was not working properly
    //LaunchedEffect(authState.value) {
    //    if (authState.value is AuthState.Unauthenticated) {
    //        navController.navigate(LoginRoute) {
    //            popUpTo(navController.graph.startDestinationId) { inclusive = true }
    //        }
    //    }
    //}

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { uiEvent ->
            if (uiEvent is UiEvent.Navigate<*> && uiEvent.route is AddEditRoute) {
                navigateToAddEditScreen(uiEvent.route.id as String?)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Tasks") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.signout()

                        navController.navigate(LoginRoute) {        // put on the button so it doesn't affect the backgesture on android
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }

                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onEvent(ListEvent.AddEdit(null)) }) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            itemsIndexed(todos) { index, todo ->
                TodoItem(
                    todo = todo,
                    onCompletedChange = { isCompleted ->
                        viewModel.onEvent(ListEvent.CompleteChanged(todo.id, isCompleted))
                    },
                    onItemClick = {
                        viewModel.onEvent(ListEvent.AddEdit(todo.id))
                    },
                    onDeleteClick = {
                        viewModel.onEvent(ListEvent.Delete(todo.id))
                    }
                )
                if (index < todos.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun ListContentPreview() {
    ToDoListTheme {
        Scaffold { paddingValues ->
            LazyColumn(
                modifier = Modifier.consumeWindowInsets(paddingValues),
                contentPadding = PaddingValues(16.dp)
            ) {
                itemsIndexed(listOf(todo1, todo2, todo3)) { index, todo ->
                    TodoItem(
                        todo = todo,
                        onCompletedChange = {},
                        onItemClick = {},
                        onDeleteClick = {}
                    )
                    if (index < 2) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

