package br.com.vieirateam.wyncef.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import br.com.vieirateam.wyncef.database.dao.ItemDAO
import br.com.vieirateam.wyncef.entity.Item
import java.util.Date

class ItemRepository(private val itemDAO: ItemDAO) {

    lateinit var items: LiveData<List<Item>>

    fun select(inventoryID: Long) {
        items = itemDAO.select(inventoryID)
    }

    fun filter(inventoryID: Long, query: String?) {
        items = itemDAO.filter(inventoryID, query)
    }

    fun filter(inventoryID: Long, startDate: Date, finalDate: Date) {
        items = itemDAO.filter(inventoryID, startDate, finalDate)
    }

    @WorkerThread
    suspend fun insert(item: Item) {
        itemDAO.insert(item)
    }

    @WorkerThread
    suspend fun update(item: Item) {
        itemDAO.update(item)
    }

    @WorkerThread
    suspend fun delete(item: Item) {
        itemDAO.delete(item)
    }

    @WorkerThread
    suspend fun deleteItems(inventoryID: Long) {
        itemDAO.deleteItems(inventoryID)
    }

    @WorkerThread
    suspend fun selectItem(serialNumber: String) = itemDAO.selectItem(serialNumber)

    @WorkerThread
    suspend fun selectItem(inventoryID: Long, serialNumber: String) = itemDAO.selectItem(inventoryID, serialNumber)
}