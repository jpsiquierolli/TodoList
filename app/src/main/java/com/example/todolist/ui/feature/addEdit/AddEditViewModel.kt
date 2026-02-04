package com.example.todolist.ui.feature.addEdit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.TodoRepository
import com.example.todolist.domain.Todo
import com.example.todolist.ui.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditViewModel(
    private val id: String? = null,
    private val repository: TodoRepository
) : ViewModel() {

    var title by mutableStateOf("")
        private set

    var description by mutableStateOf<String?>(null)
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        id?.let {
            viewModelScope.launch {
                val todo = repository.getBy(it)
                title = todo?.title ?: ""
                description = todo?.description
            }
        }
    }

    fun onEvent(event : AddEditEvent){

        when(event) {
            is AddEditEvent.TitleChanged-> {
                title = event.title
            }
            is AddEditEvent.DescriptionChanged -> {
                description = event.description
            }
            AddEditEvent.Save -> {
                saveTodo()
            }
        }
    }

    private fun saveTodo(){
        viewModelScope.launch {

            if(title.isBlank()){
                _uiEvent.send(UiEvent.ShowSnackbar(message = "The title can't be empty"))
                return@launch
            }

            repository.insert(
                id = id,
                title = title,
                description = description
            )

            _uiEvent.send(UiEvent.NavigateBack)
        }
    }
}