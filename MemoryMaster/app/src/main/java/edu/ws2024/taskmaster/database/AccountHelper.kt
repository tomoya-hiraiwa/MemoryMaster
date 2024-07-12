package edu.ws2024.taskmaster.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import edu.ws2024.taskmaster.model.Account
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountHelper(context: Context) : SQLiteOpenHelper(context, "account", null, 1) {
    private var db: SQLiteDatabase = this.writableDatabase

    companion object {
        const val tableName = "account"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.let {
            it.execSQL("create table ${tableName}(" + " id integer primary key autoincrement, name text, password text)")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    suspend fun insert(data: Account) {
        withContext(Dispatchers.IO) {
            val insertData = ContentValues().apply {
                put("name", data.name)
                put("password", data.password)
            }
            db.insert(tableName, null, insertData)
        }
    }

    suspend fun getAccount(data: Account): Account {
       return withContext(Dispatchers.IO) {
            var returnAccount = Account()
            val cursor = db.query(
                tableName, arrayOf("name", "password"), "name = ? and password = ?",
                arrayOf(data.name, data.password), null, null, null
            )
            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val password = cursor.getString(cursor.getColumnIndexOrThrow("password"))
                returnAccount.name = name
                returnAccount.password = password
            }
            cursor.close()
           returnAccount
        }
    }
}