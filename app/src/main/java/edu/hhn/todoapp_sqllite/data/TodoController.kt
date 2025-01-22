package edu.hhn.todoapp_sqllite.data


import android.content.ContentValues
import android.content.Context
import android.util.Log


class TodoController(context: Context) {
    private val dbHelper = DbHelper(context)
    private val TAG = "TodoController"

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