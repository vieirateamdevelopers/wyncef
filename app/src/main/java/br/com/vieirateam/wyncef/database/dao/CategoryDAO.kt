package br.com.vieirateam.wyncef.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import br.com.vieirateam.wyncef.entity.Category
import java.util.Date

@Dao
interface CategoryDAO : BaseDAO<Category> {

    @Query("SELECT * FROM category WHERE category_name == :query OR category_initials == :query LIMIT 1")
    suspend fun selectCategory(query: String): Category?

    @Query("SELECT * FROM category ORDER BY category_name ASC")
    fun select(): LiveData<List<Category>>

    @Query("SELECT * FROM category WHERE category_name LIKE :query OR category_initials LIKE :query ORDER BY category_name ASC")
    fun filter(query: String?): LiveData<List<Category>>

    @Query("SELECT * FROM category WHERE category_date BETWEEN :startDate AND :finalDate ORDER BY category_date ASC")
    fun filter(startDate: Date, finalDate: Date): LiveData<List<Category>>
}