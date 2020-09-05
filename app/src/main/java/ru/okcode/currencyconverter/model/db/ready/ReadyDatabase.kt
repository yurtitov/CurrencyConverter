package ru.okcode.currencyconverter.model.db.ready

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.okcode.currencyconverter.model.db.Converters

@Database(
    entities = [ReadyHeader::class, ReadyRate:: class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ReadyDatabase : RoomDatabase() {
    abstract fun readyDao(): ReadyDao
}