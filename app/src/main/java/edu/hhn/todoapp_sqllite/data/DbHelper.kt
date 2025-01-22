package edu.hhn.todoapp_sqllite.data


import android.content.ContentValues.TAG
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.FileOutputStream

class DbHelper(val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        // Diese Methode bleibt leer, da wir eine bestehende Datenbank aus den assets verwenden.
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Wenn die Datenbankversion aktualisiert wird, löschen wir die alte und kopieren sie neu.
        context.deleteDatabase(DATABASE_NAME)
        copyDatabaseFromAssets()
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        Log.d(TAG, "Getting readable database")
        copyDatabaseFromAssets()
        return super.getReadableDatabase()
    }

    override fun getWritableDatabase(): SQLiteDatabase {
        Log.d(TAG, "Getting writable database")
        copyDatabaseFromAssets()
        return super.getWritableDatabase()
    }

    private fun copyDatabaseFromAssets() {
        val dbPath = context.getDatabasePath(DATABASE_NAME)

        if (!dbPath.exists()) {
            Log.d(TAG, "Database doesn't exist, copying from assets...")
            try {
                dbPath.parentFile?.mkdirs()
                context.assets.open(DATABASE_NAME).use { input ->
                    FileOutputStream(dbPath).use { output ->
                        input.copyTo(output)
                    }
                }
                Log.d(TAG, "Database copied successfully to: ${dbPath.absolutePath}")
                Log.d(TAG, "Database size: ${dbPath.length()} bytes")
            } catch (e: Exception) {
                Log.e(TAG, "Error copying database from assets", e)
                throw RuntimeException("Error copying database", e)
            }
        } else {
            Log.d(TAG, "Database already exists at ${dbPath.absolutePath}")
        }
    }

    companion object {
        // Name der Datenbankdatei
        const val DATABASE_NAME = "todo_app.db"

        // Aktuelle Version der Datenbank
        const val DATABASE_VERSION = 1
    }
}