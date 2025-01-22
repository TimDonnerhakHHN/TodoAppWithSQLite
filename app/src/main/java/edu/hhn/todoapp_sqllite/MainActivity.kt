package edu.hhn.todoapp_sqllite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.hhn.todoapp_sqllite.ui.screens.TodoDashboard
import edu.hhn.todoapp_sqllite.viewmodel.TodoViewModel
import edu.hhn.todoapp_sqllite.viewmodel.ViewModelFactory

/**
 * The main entry point of the TodoApp application.
 *
 * This activity sets up the UI using Jetpack Compose and initializes the [TodoViewModel]
 * with a custom [ViewModelFactory].
 */
class MainActivity : ComponentActivity() {

    /**
     * Called when the activity is starting. Sets up the content view
     * with a [TodoDashboard] composable wrapped in a [MaterialTheme].
     *
     * The [TodoViewModel] is initialized using the `viewModel` function with
     * a custom [ViewModelFactory] that passes the application context.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down, this Bundle contains the most recent data
     * supplied in [onSaveInstanceState]. Otherwise, it is null.
     */
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
