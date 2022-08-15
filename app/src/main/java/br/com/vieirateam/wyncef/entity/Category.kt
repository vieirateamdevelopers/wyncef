package br.com.vieirateam.wyncef.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import br.com.vieirateam.wyncef.R
import java.util.Date

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    var id: Long = 0,
    @ColumnInfo(name = "category_name")
    var name: String = "",
    @ColumnInfo(name = "category_initials")
    var initials: String = "",
    @ColumnInfo(name = "category_icon")
    var icon: Int = R.drawable.ic_drawable_device_other,
    @ColumnInfo(name = "category_date")
    var date: Date = Date()
) : Serializable
