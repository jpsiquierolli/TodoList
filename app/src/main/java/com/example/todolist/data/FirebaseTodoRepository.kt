package com.example.todolist.data

import com.example.todolist.domain.Todo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseTodoRepository : TodoRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserId(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
    }

    private fun getTodosCollection() = firestore
        .collection("users")
        .document(getUserId())
        .collection("todos")

    override suspend fun insert(title: String, description: String?, id: Long?) {
        val todoData = hashMapOf(
            "title" to title,
            "description" to description,
            "isCompleted" to false,
            "userId" to getUserId(),
            "timestamp" to System.currentTimeMillis()
        )

        if (id != null) {
            // Update existing todo
            getTodosCollection()
                .document(id.toString())
                .set(todoData)
                .await()
        } else {
            // Create new todo
            val docRef = getTodosCollection().document()
            todoData["id"] = docRef.id
            docRef.set(todoData).await()
        }
    }

    override suspend fun updateCompleted(id: Long, isCompleted: Boolean) {
        getTodosCollection()
            .document(id.toString())
            .update("isCompleted", isCompleted)
            .await()
    }

    override suspend fun delete(id: Long) {
        getTodosCollection()
            .document(id.toString())
            .delete()
            .await()
    }

    override fun getAll(): Flow<List<Todo>> = callbackFlow {
        val listener = getTodosCollection()
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val todos = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val id = doc.id
                        Todo(
                            id = id.hashCode().toLong(),
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description"),
                            isCompleted = doc.getBoolean("isCompleted") ?: false
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(todos)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getBy(id: Long): Todo? {
        val doc = getTodosCollection()
            .document(id.toString())
            .get()
            .await()

        return if (doc.exists()) {
            Todo(
                id = id,
                title = doc.getString("title") ?: "",
                description = doc.getString("description"),
                isCompleted = doc.getBoolean("isCompleted") ?: false
            )
        } else {
            null
        }
    }
}