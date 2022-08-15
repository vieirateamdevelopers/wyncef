package br.com.vieirateam.wyncef.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import br.com.vieirateam.wyncef.database.WyncefDatabase
import br.com.vieirateam.wyncef.entity.Category
import br.com.vieirateam.wyncef.interfaces.ViewModelScope
import br.com.vieirateam.wyncef.repository.CategoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class CategoryViewModel(application: Application) : AndroidViewModel(application), ViewModelScope<Category> {

    private var categoryDAO = WyncefDatabase.getDatabase(viewModelScope).categoryDAO()
    private var categoryRepository = CategoryRepository(categoryDAO)
    lateinit var categories: LiveData<List<Category>>

    override fun getScope(): CoroutineScope = viewModelScope

    override fun select() {
        categoryRepository.select()
        categories = categoryRepository.categories
    }

    override fun insert(item: Category) = viewModelScope.launch(Dispatchers.IO) {
        categoryRepository.insert(item)
    }

    override fun update(item: Category) = viewModelScope.launch(Dispatchers.IO) {
        categoryRepository.update(item)
    }

    override fun delete(item: Category) = viewModelScope.launch(Dispatchers.IO) {
        categoryRepository.delete(item)
    }

    suspend fun selectCategory(query: String) = categoryRepository.selectCategory(query)

    fun filter(query: String?) {
        categoryRepository.filter("%$query%")
        categories = categoryRepository.categories
    }

    fun filter(startDate: Date, finalDate: Date) {
        categoryRepository.filter(startDate, finalDate)
        categories = categoryRepository.categories
    }
}