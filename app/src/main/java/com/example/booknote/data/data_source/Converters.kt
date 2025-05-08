package com.example.booknote.data.data_source

import androidx.room.TypeConverter
import com.example.booknote.domain.model.Book
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromBookStatus(value: Book.BookStatus): String {
        return value.name
    }

    @TypeConverter
    fun toBookStatus(value: String): Book.BookStatus {
        return Book.BookStatus.valueOf(value)
    }
}