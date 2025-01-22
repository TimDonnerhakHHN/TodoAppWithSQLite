package edu.hhn.todoapp_sqllite.domain.validation


import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TodoValidation {
    const val MAX_NAME_LENGTH = 50
    const val MAX_DESCRIPTION_LENGTH = 500
    const val MAX_PRIORITY_LENGTH = 20
    const val DATE_PATTERN = "dd.MM.yyyy"

    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "Name darf nicht leer sein")
            name.length > MAX_NAME_LENGTH -> ValidationResult(false, "Name darf maximal $MAX_NAME_LENGTH Zeichen lang sein")
            else -> ValidationResult(true)
        }
    }

    fun validateDescription(description: String): ValidationResult {
        return when {
            description.length > MAX_DESCRIPTION_LENGTH -> ValidationResult(
                false,
                "Beschreibung darf maximal $MAX_DESCRIPTION_LENGTH Zeichen lang sein"
            )
            else -> ValidationResult(true)
        }
    }

    fun validatePriority(priority: String): ValidationResult {
        return when {
            priority.isBlank() -> ValidationResult(false, "Priorität darf nicht leer sein")
            priority.length > MAX_PRIORITY_LENGTH -> ValidationResult(false, "Priorität darf maximal $MAX_PRIORITY_LENGTH Zeichen lang sein")
            !listOf("Hoch", "Mittel", "Niedrig").contains(priority) ->
                ValidationResult(false, "Priorität muss 'Hoch', 'Mittel' oder 'Niedrig' sein")
            else -> ValidationResult(true)
        }
    }

    fun validateDueDate(dueDate: String): ValidationResult {
        return try {
            if (dueDate.isBlank()) {
                return ValidationResult(false, "Datum darf nicht leer sein")
            }

            val formatter = SimpleDateFormat(DATE_PATTERN, Locale.GERMAN)
            formatter.isLenient = false
            val date = formatter.parse(dueDate)

            // Prüfe ob Datum in der Vergangenheit liegt
            if (date != null && date.before(Date())) {
                return ValidationResult(false, "Datum darf nicht in der Vergangenheit liegen")
            }

            ValidationResult(true)
        } catch (e: ParseException) {
            ValidationResult(false, "Ungültiges Datumsformat. Bitte TT.MM.JJJJ verwenden")
        }
    }
}