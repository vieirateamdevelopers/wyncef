package br.com.vieirateam.wyncef.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date

@Entity(tableName = "tag")
data class Tag(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tag_id")
    var id : Long = 0,
    @ColumnInfo(name = "tag_make")
    var make: String = "",
    @ColumnInfo(name = "tag_model")
    var model: String = "",
    @ColumnInfo(name = "tag_ip")
    var IP : String? = null,
    @ColumnInfo(name = "tag_date")
    var date: Date = Date(),
    @Embedded(prefix = "tag_")
    var device: Device,
    @ColumnInfo(name = "tag_complete")
    var complete : Boolean = false
) : Serializable