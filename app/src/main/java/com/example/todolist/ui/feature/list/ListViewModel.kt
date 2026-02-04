package com.example.todolist.ui.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.TodoRepository
import com.example.todolist.domain.Todo
import com.example.todolist.navigation.AddEditRoute
import com.example.todolist.ui.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListViewModel constructor(
    private val repository: TodoRepository
) : ViewModel() {

    val todos: StateFlow<List<Todo>> = repository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: ListEvent) {
        when (event) {
            is ListEvent.Delete -> {
                delete(event.id)
            }

            is ListEvent.CompleteChanged -> {
                completeChanged(event.id, event.isCompleted)
            }

            is ListEvent.AddEdit -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate(AddEditRoute(event.id)))
                }
            }
        }
    }

    private fun delete(id: String) {
        viewModelScope.launch {
            repository.delete(id)

        }
    }

    private fun completeChanged(id: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateCompleted(id, isCompleted)
        }
    }
}