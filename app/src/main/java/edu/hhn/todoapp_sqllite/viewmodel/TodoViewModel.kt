package edu.hhn.todoapp_sqllite.viewmodel


import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.hhn.todoapp_sqllite.data.Todo
import edu.hhn.todoapp_sqllite.data.TodoController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val todoController = TodoController(application.applicationContext)
    private val _todos = mutableStateListOf<Todo>()
    val todos: List<Todo> = _todos

    init {
        loadTodos()
    }

    private fun loadTodos() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Starting to load todos")
                val loadedTodos = todoController.getTodos()
                withContext(Dispatchers.Main) {
                    _todos.clear()
                    _todos.addAll(loadedTodos)
                    Log.d(TAG, "Successfully loaded ${loadedTodos.size} todos")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading todos", e)
            }
        }
    }

    fun addTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Adding new todo: ${todo.name}")
                todoController.addTodo(todo)
                loadTodos()
            } catch (e: Exception) {
                Log.e(TAG, "Error adding todo", e)
            }
        }
    }

    fun updateTodo(updatedTodo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Updating todo: ID=${updatedTodo.id}")
                todoController.updateTodo(updatedTodo)
                loadTodos()
            } catch (e: Exception) {
                Log.e(TAG, "Error updating todo", e)
            }
        }
    }

    fun deleteTodo(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Deleting todo: ID=$id")
                todoController.deleteTodo(id)
                loadTodos()
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting todo", e)
            }
        }
    }

    fun toggleTodoStatus(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val todo = _todos.find { it.id == id }
            todo?.let {
                val updatedTodo = it.copy(isCompleted = !it.isCompleted)
                todoController.updateTodo(updatedTodo)
                loadTodos()
            }
        }
    }
}