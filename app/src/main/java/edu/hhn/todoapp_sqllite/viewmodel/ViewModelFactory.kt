package edu.hhn.todoapp_sqllite.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory class for creating instances of [ViewModel] with specific dependencies.
 *
 * This factory is particularly useful for creating instances of [TodoViewModel] with an
 * application context as a dependency.
 *
 * @param application The application context to be passed to the ViewModel.
 */
class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the specified [ViewModel] class.
     *
     * @param modelClass The class of the [ViewModel] to create.
     * @return An instance of the specified [ViewModel] type.
     * @throws IllegalArgumentException If the [modelClass] is not assignable from [TodoViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
