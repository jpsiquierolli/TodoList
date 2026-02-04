package com.example.todolist.data

import com.example.todolist.domain.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {

    suspend fun insert(title: String, description: String?, id: String? = null)

    suspend fun updateCompleted(id: String, isCompleted: Boolean)

    suspend fun delete(id: String)

    fun getAll(): Flow<List<Todo>>

    suspend fun getBy(id: String): Todo?
}