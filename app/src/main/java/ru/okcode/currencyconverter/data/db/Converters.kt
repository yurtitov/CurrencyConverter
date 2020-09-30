package ru.okcode.currencyconverter.data.db

import android.icu.math.BigDecimal
import androidx.room.TypeConverter
import java.util.*
import java.util.stream.Collectors

class Converters {

    @TypeConverter
    fun fromListStringToString(value: List<String>): String {
        return value.stream().collect(Collectors.joining(","))
    }

    @TypeConverter
    fun fromStringToListString(value: String): List<String> {
        return ArrayList(value.split(","))
    }
}