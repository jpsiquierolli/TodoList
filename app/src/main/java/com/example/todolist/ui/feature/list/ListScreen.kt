package com.example.todolist.ui.feature.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todolist.domain.Todo
import com.example.todolist.domain.todo1
import com.example.todolist.domain.todo2
import com.example.todolist.domain.todo3
import com.example.todolist.navigation.AddEditRoute
import com.example.todolist.ui.UiEvent
import com.example.todolist.ui.components.TodoItem
import com.example.todolist.ui.theme.ToDoListTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.todolist.data.TodoDatabaseProvider
import com.example.todolist.data.TodoRepositoryImpl
import com.example.todolist.ui.feature.pages.AuthState
import com.example.todolist.ui.feature.pages.AuthViewModel

@Composable
fun ListScreen(
    navigateToAddEditScreen: (id: Long?) -> Unit, modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel
) {
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current.applicationContext
    val database = TodoDatabaseProvider.provide(context)
    val repository = TodoRepositoryImpl(database.todoDao)
    val viewModel = viewModel<ListViewModel>() {
        ListViewModel(repository = repository)
    }
    val todos by viewModel.todos.collectAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Home Page", fontSize = 32.sp)

        TextButton(onClick = {
            authViewModel.signout()
        }) {
            Text(text = "Sign out")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { uiEvent ->
            when (uiEvent) {
                is UiEvent.Navigate<*> -> {
                    when (uiEvent.route) {
                        is AddEditRoute -> {
                            navigateToAddEditScreen(uiEvent.route.id)
                        }
                    }
                }
                is UiEvent.NavigateBack -> {

                }
                is UiEvent.ShowSnackbar -> {

                }
            }
        }
    }

    ListContent(
        todos = todos,
        onEvent = viewModel::onEvent,
    )
}

@Composable
fun ListContent(
    todos: List<Todo>,
    onEvent: (ListEvent) -> Unit,
) {
    Scaffold (
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(ListEvent.AddEdit(null))
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) {paddingValues ->
        LazyColumn(
            modifier = Modifier
                .consumeWindowInsets(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            itemsIndexed(todos) {index, todo ->
                TodoItem(
                    todo = todo,
                    onCompletedChange = {
                        onEvent(ListEvent.CompleteChanged(todo.id, it))
                    },
                    onItemClick = {
                        onEvent(ListEvent.AddEdit(todo.id))
                    },
                    onDeleteClick = {
                        onEvent(ListEvent.Delete(todo.id))
                    }
                )

                if(index < todos.lastIndex) {
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
        ListContent(
            todos = listOf(
                todo1,
                todo2,
                todo3
            ),

            onEvent = {},
        )
    }
}
