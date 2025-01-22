package edu.hhn.todoapp_sqllite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.hhn.todoapp_sqllite.ui.screens.TodoDashboard
import edu.hhn.todoapp_sqllite.viewmodel.TodoViewModel
import edu.hhn.todoapp_sqllite.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val viewModel: TodoViewModel = viewModel(
                    factory = ViewModelFactory(application)
                )
                TodoDashboard(viewModel = viewModel)
            }
        }
    }
}