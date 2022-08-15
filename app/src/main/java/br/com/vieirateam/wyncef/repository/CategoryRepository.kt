package br.com.vieirateam.wyncef.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import br.com.vieirateam.wyncef.database.dao.CategoryDAO
import br.com.vieirateam.wyncef.entity.Category
import java.util.Date

class CategoryRepository(private val categoryDAO: CategoryDAO) {

    lateinit var categories: LiveData<List<Category>>

    fun select() {
        categories = categoryDAO.select()
    }

    fun filter(query: String?) {
        categories = categoryDAO.filter(query)
    }

    fun filter(startDate: Date, finalDate: Date) {
        categories = categoryDAO.filter(startDate, finalDate)
    }

    @WorkerThread
    suspend fun insert(category: Category) {
        categoryDAO.insert(category)
    }

    @WorkerThread
    suspend fun update(category: Category) {
        categoryDAO.update(category)
    }

    @WorkerThread
    suspend fun delete(category: Category) {
        categoryDAO.delete(category)
    }

    @WorkerThread
    suspend fun selectCategory(query: String) = categoryDAO.selectCategory(query)
}