package edu.hhn.todoapp_sqllite.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import edu.hhn.todoapp_sqllite.data.Todo

/**
 * Displays a card representing a single to-do item with actions for status toggle, edit, and delete.
 *
 * @param todo The [Todo] object containing the details to display.
 * @param onDelete A callback function invoked when the delete button is clicked, passing the to-do ID.
 * @param onEdit A callback function invoked when the edit button is clicked, passing the [Todo] object.
 * @param onToggleStatus A callback function invoked when the status toggle button is clicked, passing the to-do ID.
 */
@Composable
fun TodoCard(
    todo: Todo,
    onDelete: (Int) -> Unit,
    onEdit: (Todo) -> Unit,
    onToggleStatus: (Int) -> Unit
) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(todo.name, style = MaterialTheme.typography.bodyMedium)
            Text("Priorität: ${todo.priority}")
            Text("Endzeitpunkt: ${todo.dueDate}")
            Text("Beschreibung: ${todo.description}")
            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Text(
                    if (todo.isCompleted) "Erledigt" else "Offen",
                    color = if (todo.isCompleted) Color.Green else Color.Red
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { onToggleStatus(todo.id) }) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Status umschalten")
                }
                IconButton(onClick = { onEdit(todo) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Bearbeiten")
                }
                IconButton(onClick = { onDelete(todo.id) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Löschen")
                }
            }
        }
    }
}
