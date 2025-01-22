package edu.hhn.todoapp_sqllite.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.hhn.todoapp_sqllite.data.Todo
import edu.hhn.todoapp_sqllite.data.TodoController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for managing to-do items in the application.
 *
 * This ViewModel is responsible for interacting with the data layer through the [TodoController],
 * managing the state of the to-do list, and providing functions for common operations like adding,
 * updating, deleting, and toggling the completion status of to-do items.
 *
 * @constructor Creates a new instance of [TodoViewModel].
 * @param application The application context, passed to the ViewModel for initializing dependencies.
 */
class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val todoController = TodoController(application.applicationContext)
    private val _todos = mutableStateListOf<Todo>()

    /** Publicly accessible list of to-do items. */
    val todos: List<Todo> = _todos

    init {
        loadTodos()
    }

    /**
     * Loads the list of to-do items from the data layer and updates the state.
     */
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

    /**
     * Adds a new to-do item to the data layer and reloads the state.
     *
     * @param todo The new [Todo] to add.
     */
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

    /**
     * Updates an existing to-do item in the data layer and reloads the state.
     *
     * @param updatedTodo The [Todo] with updated properties.
     */
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

    /**
     * Deletes a to-do item by its ID and reloads the state.
     *
     * @param id The ID of the to-do item to delete.
     */
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

    /**
     * Toggles the completion status of a to-do item and reloads the state.
     *
     * @param id The ID of the to-do item whose status is to be toggled.
     */
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
