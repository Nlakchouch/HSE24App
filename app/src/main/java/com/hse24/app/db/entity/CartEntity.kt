package com.hse24.app.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Cart")
data class CartEntity (@PrimaryKey var sku: String, var quantity: Int)

/*{

    constructor(sku: String, quantity: Int) : this(){
        this.sku = sku
        this.quantity = quantity
    }
} */