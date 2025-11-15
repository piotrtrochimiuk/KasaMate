package com.studia.kasamate.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val description: String,
    val price: Double,
    val date: String,
    val currency: String = "PLN",
    val username: String
)
