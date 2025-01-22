package edu.hhn.todoapp_sqllite.data

import android.content.ContentValues
import android.content.Context
import android.util.Log

/**
 * Controller class for managing CRUD operations on the `todos` table in the SQLite database.
 *
 * This class provides methods to retrieve, add, update, and delete todos from the database.
 *
 * @param context The application context for accessing the database.
 */
class TodoController(context: Context) {
    private val dbHelper = DbHelper(context)
    private val TAG = "TodoController"

    /**
     * Retrieves all todos from the database.
     *
     * @return A list of [Todo] objects retrieved from the database.
     */
    fun getTodos(): List<Todo> {
        val todos = mutableListOf<Todo>()
        val db = dbHelper.readableDatabase

        try {
            val cursor = db.query(
                "todos",
                arrayOf("id", "name", "priority", "due_date", "description", "is_completed"),
                null, null, null, null, null
            )

            val count = cursor.count
            Log.d(TAG, "Attempting to read todos from database. Found $count entries.")

            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val priority = cursor.getString(cursor.getColumnIndexOrThrow("priority"))
                val dueDate = cursor.getString(cursor.getColumnIndexOrThrow("due_date"))
                val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                val isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("is_completed")) == 1

                todos.add(Todo(id, name, priority, dueDate, description, isCompleted))
                Log.d(TAG, "Read todo: ID=$id, Name=$name")
            }

            cursor.close()
            Log.d(TAG, "Successfully read ${todos.size} todos from database")
        } catch (e: Exception) {
            Log.e(TAG, "Error reading todos from database", e)
        } finally {
            db.close()
        }

        return todos
    }

    /**
     * Adds a new todo to the database.
     *
     * @param todo The [Todo] object to be added.
     */
    fun addTodo(todo: Todo) {
        val db = dbHelper.writableDatabase

        try {
            val values = ContentValues().apply {
                put("name", todo.name)
                put("priority", todo.priority)
                put("due_date", todo.dueDate)
                put("description", todo.description)
                put("is_completed", if (todo.isCompleted) 1 else 0)
            }

            val newId = db.insert("todos", null, values)
            if (newId != -1L) {
                Log.d(TAG, "Successfully added new todo: ID=$newId, Name=${todo.name}")
            } else {
                Log.e(TAG, "Failed to add todo: ${todo.name}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding todo: ${todo.name}", e)
        } finally {
            db.close()
        }
    }

    /**
     * Updates an existing todo in the database.
     *
     * @param todo The [Todo] object containing updated information.
     */
    fun updateTodo(todo: Todo) {
        val db = dbHelper.writableDatabase

        try {
            val values = ContentValues().apply {
                put("name", todo.name)
                put("priority", todo.priority)
                put("due_date", todo.dueDate)
                put("description", todo.description)
                put("is_completed", if (todo.isCompleted) 1 else 0)
            }

            val rowsAffected = db.update("todos", values, "id = ?", arrayOf(todo.id.toString()))
            if (rowsAffected > 0) {
                Log.d(TAG, "Successfully updated todo: ID=${todo.id}, Name=${todo.name}")
            } else {
                Log.e(TAG, "Failed to update todo: ID=${todo.id}, Name=${todo.name}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating todo: ID=${todo.id}", e)
        } finally {
            db.close()
        }
    }

    /**
     * Deletes a todo from the database by its ID.
     *
     * @param id The ID of the todo to be deleted.
     */
    fun deleteTodo(id: Int) {
        val db = dbHelper.writableDatabase

        try {
            val rowsDeleted = db.delete("todos", "id = ?", arrayOf(id.toString()))
            if (rowsDeleted > 0) {
                Log.d(TAG, "Successfully deleted todo with ID=$id")
            } else {
                Log.e(TAG, "Failed to delete todo with ID=$id")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting todo with ID=$id", e)
        } finally {
            db.close()
        }
    }
}
