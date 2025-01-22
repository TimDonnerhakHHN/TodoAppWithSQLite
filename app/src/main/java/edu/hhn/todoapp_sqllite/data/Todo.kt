package edu.hhn.todoapp_sqllite.data

import androidx.compose.runtime.Immutable

/**
 * Represents a to-do item with relevant attributes to manage tasks.
 *
 * @property id The unique identifier for the to-do item.
 * @property name The name or title of the to-do item.
 * @property priority The priority level of the to-do item (e.g., "High", "Medium", "Low").
 * @property dueDate The due date for the to-do item, formatted as a string.
 * @property description A detailed description of the to-do item.
 * @property isCompleted A flag indicating whether the to-do item is completed (default is `false`).
 */
@Immutable
data class Todo(
    val id: Int,
    val name: String,
    val priority: String,
    val dueDate: String,
    val description: String,
    var isCompleted: Boolean = false
)
