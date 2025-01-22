package edu.hhn.todoapp_sqllite.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import edu.hhn.todoapp_sqllite.data.Todo
import edu.hhn.todoapp_sqllite.data.TodoController
import edu.hhn.todoapp_sqllite.domain.validation.TodoValidation
import edu.hhn.todoapp_sqllite.viewmodel.TodoViewModel


@Composable
fun TodoDashboard(viewModel: TodoViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) } // 0 für Aktive, 1 für Erledigte
    var isEditDialogVisible by remember { mutableStateOf(false) }
    var todoToEdit by remember { mutableStateOf<Todo?>(null) }
    val context = LocalContext.current
    val todoController = TodoController(context)
    val todos = remember { mutableStateListOf<Todo>() }

    LaunchedEffect(Unit) {
        todos.clear()
        todos.addAll(todoController.getTodos())
    }

    Column {
        // TabRow für die Navigation
        TabRow(selectedTabIndex = selectedTabIndex, modifier = Modifier.fillMaxWidth()) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Aktive ToDos") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Erledigte ToDos") }
            )
        }

        // LazyColumn um die ToDos anzuzeigen
        LazyColumn {
            val filteredTodos = if (selectedTabIndex == 0) {
                viewModel.todos.filter { !it.isCompleted } // Nur offene ToDos
            } else {
                viewModel.todos.filter { it.isCompleted } // Nur erledigte ToDos
            }

            items(filteredTodos) { todo ->
                TodoCard(
                    todo = todo,
                    onDelete = { viewModel.deleteTodo(it) },
                    onEdit = { todoToEdit = it; isEditDialogVisible = true }, // Editieren eines ToDos
                    onToggleStatus = { viewModel.toggleTodoStatus(it) }
                )
            }
        }

        // FloatingActionButton für das Hinzufügen eines neuen ToDos
        if (selectedTabIndex == 0) {
            FloatingActionButton(onClick = {
                todoToEdit = Todo(
                    id = -1, // temporäre ID, wird vom ViewModel überschrieben
                    name = "",
                    priority = "",
                    dueDate = "",
                    description = ""
                )
                isEditDialogVisible = true
            }, modifier = Modifier.padding(16.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Neues ToDo hinzufügen")
            }
        }

        // Zeige den Bearbeitungs-Dialog, wenn er sichtbar sein soll
        if (isEditDialogVisible && todoToEdit != null) {
            TodoEditDialog(
                todo = todoToEdit!!,
                onDismiss = { isEditDialogVisible = false },
                onSave = { updatedTodo ->
                    if (updatedTodo.id == -1) {
                        // Neues Todo hinzufügen
                        viewModel.addTodo(
                            updatedTodo.copy(id = (viewModel.todos.size + 1)) // ID setzen
                        )
                    } else {
                        // Existierendes Todo aktualisieren
                        viewModel.updateTodo(updatedTodo)
                    }
                    isEditDialogVisible = false
                }
            )
        }
    }
}

// Der Bearbeitungsdialog für Todos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoEditDialog(
    todo: Todo,
    onDismiss: () -> Unit,
    onSave: (Todo) -> Unit
) {
    var editedTodo by remember { mutableStateOf(todo) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var priorityError by remember { mutableStateOf<String?>(null) }
    var dueDateError by remember { mutableStateOf<String?>(null) }
    var expandedPriorityMenu by remember { mutableStateOf(false) }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bearbeite ToDo") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Name TextField mit Counter und Error
                OutlinedTextField(
                    value = editedTodo.name,
                    onValueChange = {
                        if (it.length <= TodoValidation.MAX_NAME_LENGTH) {
                            editedTodo = editedTodo.copy(name = it)
                            nameError = TodoValidation.validateName(it).errorMessage
                        }
                    },
                    label = { Text("Name*") },
                    isError = nameError != null,
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (nameError != null) Text(nameError!!, color = MaterialTheme.colorScheme.error)
                            Text("${editedTodo.name.length}/${TodoValidation.MAX_NAME_LENGTH}")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Priority Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedPriorityMenu,
                    onExpandedChange = { expandedPriorityMenu = !expandedPriorityMenu }
                ) {
                    OutlinedTextField(
                        value = editedTodo.priority,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Priorität*") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPriorityMenu) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPriorityMenu,
                        onDismissRequest = { expandedPriorityMenu = false }
                    ) {
                        listOf("Hoch", "Mittel", "Niedrig").forEach { priority ->
                            DropdownMenuItem(
                                text = { Text(priority) },
                                onClick = {
                                    editedTodo = editedTodo.copy(priority = priority)
                                    priorityError = null
                                    expandedPriorityMenu = false  // Schließe das Menü nach Auswahl
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }

                // Datum TextField mit Validierung
                OutlinedTextField(
                    value = editedTodo.dueDate,
                    onValueChange = {
                        editedTodo = editedTodo.copy(dueDate = it)
                        dueDateError = TodoValidation.validateDueDate(it).errorMessage
                    },
                    label = { Text("Endzeitpunkt (TT.MM.JJJJ)*") },
                    isError = dueDateError != null,
                    supportingText = {
                        if (dueDateError != null) Text(dueDateError!!, color = MaterialTheme.colorScheme.error)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Description TextField mit Counter
                OutlinedTextField(
                    value = editedTodo.description,
                    onValueChange = {
                        if (it.length <= TodoValidation.MAX_DESCRIPTION_LENGTH) {
                            editedTodo = editedTodo.copy(description = it)
                            descriptionError = TodoValidation.validateDescription(it).errorMessage
                        }
                    },
                    label = { Text("Beschreibung") },
                    isError = descriptionError != null,
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (descriptionError != null) Text(descriptionError!!, color = MaterialTheme.colorScheme.error)
                            Text("${editedTodo.description.length}/${TodoValidation.MAX_DESCRIPTION_LENGTH}")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                // Status Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Status:")
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = editedTodo.isCompleted,
                        onCheckedChange = { editedTodo = editedTodo.copy(isCompleted = it) }
                    )
                    Text(if (editedTodo.isCompleted) "Erledigt" else "Offen")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validiere alle Felder vor dem Speichern
                    val nameValidation = TodoValidation.validateName(editedTodo.name)
                    val priorityValidation = TodoValidation.validatePriority(editedTodo.priority)
                    val dueDateValidation = TodoValidation.validateDueDate(editedTodo.dueDate)
                    val descriptionValidation = TodoValidation.validateDescription(editedTodo.description)

                    nameError = nameValidation.errorMessage
                    priorityError = priorityValidation.errorMessage
                    dueDateError = dueDateValidation.errorMessage
                    descriptionError = descriptionValidation.errorMessage

                    // Nur speichern wenn alle Validierungen erfolgreich
                    if (nameValidation.isValid && priorityValidation.isValid &&
                        dueDateValidation.isValid && descriptionValidation.isValid) {
                        onSave(editedTodo)
                        onDismiss()
                    }
                }
            ) {
                Text("Speichern")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}
