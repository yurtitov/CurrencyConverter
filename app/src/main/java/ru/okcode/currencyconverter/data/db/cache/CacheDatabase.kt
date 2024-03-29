package ru.okcode.currencyconverter.data.db.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.okcode.currencyconverter.data.db.Converters

@Database(
    entities = [CacheRatesHeader::class, CacheCurrencyRate::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun cacheDao(): CacheDao
}