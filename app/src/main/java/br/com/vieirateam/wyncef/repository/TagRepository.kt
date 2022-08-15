package br.com.vieirateam.wyncef.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import br.com.vieirateam.wyncef.database.dao.TagDAO
import br.com.vieirateam.wyncef.entity.Tag
import java.util.Date

class TagRepository(private val tagDAO: TagDAO) {

    lateinit var tags: LiveData<List<Tag>>

    fun select() {
        tags = tagDAO.select()
    }

    fun filter(query: String?) {
        tags = tagDAO.filter(query)
    }

    fun filter(startDate: Date, finalDate: Date) {
        tags = tagDAO.filter(startDate, finalDate)
    }

    @WorkerThread
    suspend fun insert(tag: Tag) {
        tagDAO.insert(tag)
    }

    @WorkerThread
    suspend fun update(tag: Tag) {
        tagDAO.update(tag)
    }

    @WorkerThread
    suspend fun delete(tag: Tag) {
        tagDAO.delete(tag)
    }

    @WorkerThread
    suspend fun selectTag(query: String) = tagDAO.selectTag(query)
}