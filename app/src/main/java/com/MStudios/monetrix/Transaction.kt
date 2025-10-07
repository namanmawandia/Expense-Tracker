package com.MStudios.monetrix

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction (
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val amount:Double,
    val date : Long,
    val note : String,
    val category : Int,
    val type : Int)