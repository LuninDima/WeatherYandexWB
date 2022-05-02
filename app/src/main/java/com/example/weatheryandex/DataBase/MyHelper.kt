package com.example.weatheryandex.DataBase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyHelper(context: Context?): SQLiteOpenHelper(
    context, "WEATHER_DB", null, 1
) {
    override fun onCreate(db: SQLiteDatabase) {
        db?.execSQL("CREATE TABLE WEATHER_DB(_id INTEGER, LAT REAL, LON REAL, CONDITION TEXT, TEMPERATURE INTEGER, FEELSLIKE INTEGER)")
        db?.execSQL("INSERT INTO WEATHER_DB(_id, LAT, LON, CONDITION, TEMPERATURE, FEELSLIKE) VALUES(1, 20.00, 21.00, 'облачно', 100, 105)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}