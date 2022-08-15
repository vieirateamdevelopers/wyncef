package br.com.vieirateam.wyncef.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import br.com.vieirateam.wyncef.entity.Tag
import java.util.Date

@Dao
interface TagDAO : BaseDAO<Tag>{

    @Query("SELECT * FROM tag WHERE tag_device_serial_number == :query LIMIT 1")
    suspend fun selectTag(query: String): Tag?

    @Query("SELECT * FROM tag ORDER BY tag_id DESC")
    fun select(): LiveData<List<Tag>>

    @Query("SELECT * FROM tag WHERE tag_make LIKE :query OR tag_model LIKE :query OR tag_device_agency_name LIKE :query OR tag_device_agency_cgc LIKE :query OR tag_device_category_name LIKE :query OR tag_device_category_initials LIKE :query OR tag_device_serial_number LIKE :query OR tag_device_logical_name LIKE :query OR tag_device_patrimony LIKE :query ORDER BY tag_id DESC")
    fun filter(query: String?): LiveData<List<Tag>>

    @Query("SELECT * FROM tag WHERE tag_date BETWEEN :startDate AND :finalDate ORDER BY tag_id DESC")
    fun filter(startDate: Date, finalDate: Date): LiveData<List<Tag>>
}