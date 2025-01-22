package edu.hhn.todoapp_sqllite.data

import android.content.ContentValues.TAG
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.FileOutputStream

/**
 * Helper class for managing the SQLite database in the application.
 *
 * This class provides functionality to handle database creation, upgrades, and ensures the database
 * is copied from the assets folder if it doesn't already exist.
 *
 * @param context The application context used for accessing assets and database paths.
 */
class DbHelper(val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    /**
     * Called when the database is created for the first time.
     *
     * This method is empty because the database is pre-built and copied from the assets folder.
     *
     * @param db The SQLite database instance.
     */
    override fun onCreate(db: SQLiteDatabase?) {
        // This method remains empty since we use a pre-existing database from the assets folder.
    }

    /**
     * Called when the database needs to be upgraded.
     *
     * Deletes the existing database and re-copies it from the assets folder if the database version changes.
     *
     * @param db The SQLite database instance.
     * @param oldVersion The previous version of the database.
     * @param newVersion The new version of the database.
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        context.deleteDatabase(DATABASE_NAME)
        copyDatabaseFromAssets()
    }

    /**
     * Returns a readable instance of the database.
     * Copies the database from the assets folder if it does not already exist.
     *
     * @return A readable [SQLiteDatabase] instance.
     */
    override fun getReadableDatabase(): SQLiteDatabase {
        Log.d(TAG, "Getting readable database")
        copyDatabaseFromAssets()
        return super.getReadableDatabase()
    }

    /**
     * Returns a writable instance of the database.
     * Copies the database from the assets folder if it does not already exist.
     *
     * @return A writable [SQLiteDatabase] instance.
     */
    override fun getWritableDatabase(): SQLiteDatabase {
        Log.d(TAG, "Getting writable database")
        copyDatabaseFromAssets()
        return super.getWritableDatabase()
    }

    /**
     * Copies the database from the assets folder to the application's database directory.
     *
     * This method checks if the database already exists and only performs the copy if it does not.
     */
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
        /** Name of the database file. */
        const val DATABASE_NAME = "todo_app.db"

        /** Current version of the database. */
        const val DATABASE_VERSION = 1
    }
}
