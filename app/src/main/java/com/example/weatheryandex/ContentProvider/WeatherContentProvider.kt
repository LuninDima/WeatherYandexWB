package com.example.weatheryandex.ContentProvider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.example.weatheryandex.DataBase.MyHelper

/**
 * ContentProvider позволяет делиться данными базы данных WEATHER_DB с другими приложениями устройства пользователя.
 *
 *
 */

class WeatherContentProvider: ContentProvider()  {
    // создаем экземпляр класса SQLiteDatabase для работы с базой данных
    lateinit var db: SQLiteDatabase

    override fun onCreate(): Boolean {
            // создаем экземпляр класса базы данных
        var helper = MyHelper(context)
        db = helper.writableDatabase
        return db != null
    }

    //  получаем данные и возвращаем их в функцию, которая вызывает функцию query
    override fun query(
        uri: Uri,
        columns: Array<out String>?,
        condition: String?,
        condition_val: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return  db.query("WEATHER_DB", columns, condition, condition_val, null, null, sortOrder)
    }


    override fun getType(uri: Uri): String? {
       return "vnd.android.cursor.dir/vnd.example.weather_db"
    }

    // реализация функции добавления данных в базу данных
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        db.insert("WEATHER_DB", null, values)
    context?.contentResolver?.notifyChange(uri, null)
        return uri
    }
    //
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }
    // реализация функции обновления данных
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
       var count = db.update("WEATHER_DB", values, selection, selectionArgs)
        context?.contentResolver?.notifyChange(uri, null)
        return count
    }



    companion object{
        val PROVIDER_NAME = "com.example.weatheryandex/WeatherContentProvider"
        val URL = "content://$PROVIDER_NAME/WEATHER_DB"
        val CONTENT_URI: Uri = Uri.parse(URL)

        val _ID = "_id"
        val LAT = "LAT"
        val LON = "LON"
        val CONDITION = "CONDITION"
        val TEMPERATURE = "TEMPERATURE"
        val FEELSLIKE = "FEELSLIKE"
    }
}