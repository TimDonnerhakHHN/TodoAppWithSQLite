package edu.hhn.todoapp_sqllite.data
import androidx.compose.runtime.Immutable

@Immutable
data class Todo(
    val id: Int,
    val name: String,
    val priority: String,
    val dueDate: String,
    val description: String,
    var isCompleted: Boolean = false
)
