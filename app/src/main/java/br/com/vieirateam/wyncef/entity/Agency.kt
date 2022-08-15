package br.com.vieirateam.wyncef.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date

@Entity(tableName = "agency")
data class Agency(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "agency_id")
    var id: Long = 0,
    @ColumnInfo(name = "agency_cgc")
    var cgc: String = "",
    @ColumnInfo(name = "agency_name")
    var name: String = "",
    @ColumnInfo(name = "agency_state")
    var state: String = "",
    @ColumnInfo(name = "agency_date")
    var date: Date = Date()
) : Serializable