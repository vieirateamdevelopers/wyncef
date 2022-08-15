package br.com.vieirateam.wyncef.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "baseItem")
data class Item(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "item_id")
    var id: Long = 0,
    @ColumnInfo(name = "item_order")
    var order : Long = 0,
    @ColumnInfo(name = "item_verified")
    var verified: Boolean = false,
    @Embedded(prefix = "item_")
    var inventory: Inventory,
    @Embedded(prefix = "item_")
    var device: Device
) : Serializable