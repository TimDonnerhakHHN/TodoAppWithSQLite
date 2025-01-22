package edu.hhn.todoapp_sqllite.domain.validation

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Provides validation utilities for validating fields in a to-do application.
 */
object TodoValidation {
    /** Maximum allowed length for the name field. */
    const val MAX_NAME_LENGTH = 50

    /** Maximum allowed length for the description field. */
    const val MAX_DESCRIPTION_LENGTH = 500

    /** Maximum allowed length for the priority field. */
    const val MAX_PRIORITY_LENGTH = 20

    /** The expected date format pattern (e.g., "dd.MM.yyyy"). */
    const val DATE_PATTERN = "dd.MM.yyyy"

    /**
     * Represents the result of a validation operation.
     *
     * @property isValid Indicates whether the validation passed.
     * @property errorMessage An optional error message if the validation failed.
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    /**
     * Validates the name of a to-do item.
     *
     * @param name The name to validate.
     * @return A [ValidationResult] indicating success or failure.
     */
    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "Name darf nicht leer sein")
            name.length > MAX_NAME_LENGTH -> ValidationResult(false, "Name darf maximal $MAX_NAME_LENGTH Zeichen lang sein")
            else -> ValidationResult(true)
        }
    }

    /**
     * Validates the description of a to-do item.
     *
     * @param description The description to validate.
     * @return A [ValidationResult] indicating success or failure.
     */
    fun validateDescription(description: String): ValidationResult {
        return when {
            description.length > MAX_DESCRIPTION_LENGTH -> ValidationResult(
                false,
                "Beschreibung darf maximal $MAX_DESCRIPTION_LENGTH Zeichen lang sein"
            )
            else -> ValidationResult(true)
        }
    }

    /**
     * Validates the priority of a to-do item.
     *
     * @param priority The priority to validate.
     * @return A [ValidationResult] indicating success or failure.
     */
    fun validatePriority(priority: String): ValidationResult {
        return when {
            priority.isBlank() -> ValidationResult(false, "Priorität darf nicht leer sein")
            priority.length > MAX_PRIORITY_LENGTH -> ValidationResult(false, "Priorität darf maximal $MAX_PRIORITY_LENGTH Zeichen lang sein")
            !listOf("Hoch", "Mittel", "Niedrig").contains(priority) ->
                ValidationResult(false, "Priorität muss 'Hoch', 'Mittel' oder 'Niedrig' sein")
            else -> ValidationResult(true)
        }
    }

    /**
     * Validates the due date of a to-do item.
     *
     * @param dueDate The due date to validate, formatted as "dd.MM.yyyy".
     * @return A [ValidationResult] indicating success or failure.
     */
    fun validateDueDate(dueDate: String): ValidationResult {
        return try {
            if (dueDate.isBlank()) {
                return ValidationResult(false, "Datum darf nicht leer sein")
            }

            val formatter = SimpleDateFormat(DATE_PATTERN, Locale.GERMAN)
            formatter.isLenient = false
            val date = formatter.parse(dueDate)

            // Check if the date is in the past
            if (date != null && date.before(Date())) {
                return ValidationResult(false, "Datum darf nicht in der Vergangenheit liegen")
            }

            ValidationResult(true)
        } catch (e: ParseException) {
            ValidationResult(false, "Ungültiges Datumsformat. Bitte TT.MM.JJJJ verwenden")
        }
    }
}
