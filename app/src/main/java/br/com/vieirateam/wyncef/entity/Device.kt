package br.com.vieirateam.wyncef.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date

@Entity(tableName = "device")
data class Device(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "device_id")
    var id: Long = 0,
    @ColumnInfo(name = "device_serial_number")
    var serialNumber: String = "",
    @ColumnInfo(name = "device_logical_name")
    var logicalName: String? = null,
    @ColumnInfo(name = "device_patrimony")
    var patrimony: String? = null,
    @ColumnInfo(name = "device_status")
    var status: String = "",
    @ColumnInfo(name = "device_date")
    var date: Date = Date(),
    @Embedded(prefix = "device_")
    var agency: Agency,
    @Embedded(prefix = "device_")
    var category: Category
) : Serializable