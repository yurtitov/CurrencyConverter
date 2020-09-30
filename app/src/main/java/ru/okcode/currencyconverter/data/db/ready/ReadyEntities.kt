package ru.okcode.currencyconverter.data.db.ready

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class ReadyHeader(
    val baseCurrencyCode: String,
    val baseCurrencyAmount: Float = 1f,
    val baseCurrencyRateToEuro: Float,
    @PrimaryKey val timeLastUpdateUnix: Long,
    val timeNextUpdateUnix: Long
)

@Entity
data class ReadyRate(
    @PrimaryKey val currencyCode: String,
    val rateToBase: Float,
    val rateToEuro: Float,
    val sum: Float,
    val timeLastUpdateUnix: Long,
    val priorityPosition: Int
)

data class ReadyHeaderWithRates(
    @Embedded val readyHeader: ReadyHeader,
    @Relation(
        parentColumn = "timeLastUpdateUnix",
        entityColumn = "timeLastUpdateUnix"
    )
    val rates: List<ReadyRate>
)