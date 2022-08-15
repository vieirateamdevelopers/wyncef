package br.com.vieirateam.wyncef.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date

@Entity(tableName = "inventory")
data class Inventory(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "inventory_id")
    var id: Long = 0,
    @ColumnInfo(name = "inventory_path")
    var path: String? = null,
    @ColumnInfo(name = "inventory_date")
    var date: Date = Date(),
    @Embedded(prefix = "inventory_")
    var agency: Agency,
    @ColumnInfo(name = "inventory_import")
    var importFile: Boolean = true
) : Serializable