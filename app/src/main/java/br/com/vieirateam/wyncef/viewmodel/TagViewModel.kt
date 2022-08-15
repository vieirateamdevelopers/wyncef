package br.com.vieirateam.wyncef.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import br.com.vieirateam.wyncef.database.WyncefDatabase
import br.com.vieirateam.wyncef.entity.Tag
import br.com.vieirateam.wyncef.interfaces.ViewModelScope
import br.com.vieirateam.wyncef.repository.TagRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class TagViewModel(application: Application) : AndroidViewModel(application), ViewModelScope<Tag> {

    private var tagDAO = WyncefDatabase.getDatabase(viewModelScope).tagDAO()
    private var tagRepository = TagRepository(tagDAO)
    lateinit var tags: LiveData<List<Tag>>

    override fun getScope(): CoroutineScope = viewModelScope

    override fun select() {
        tagRepository.select()
        tags = tagRepository.tags
    }

    override fun insert(item: Tag) = viewModelScope.launch(Dispatchers.IO) {
        tagRepository.insert(item)
    }

    override fun update(item: Tag) = viewModelScope.launch(Dispatchers.IO) {
        tagRepository.update(item)
    }

    override fun delete(item: Tag) = viewModelScope.launch(Dispatchers.IO) {
        tagRepository.delete(item)
    }

    suspend fun selectTag(query: String) = tagRepository.selectTag(query)

    fun filter(query: String?) {
        tagRepository.filter("%$query%")
        tags = tagRepository.tags
    }

    fun filter(startDate: Date, finalDate: Date) {
        tagRepository.filter(startDate, finalDate)
        tags = tagRepository.tags
    }
}