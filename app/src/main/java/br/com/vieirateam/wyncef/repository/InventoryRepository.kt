package br.com.vieirateam.wyncef.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import br.com.vieirateam.wyncef.database.dao.InventoryDAO
import br.com.vieirateam.wyncef.entity.Inventory
import java.util.Date

class InventoryRepository(private val inventoryDAO: InventoryDAO) {

    lateinit var inventories: LiveData<List<Inventory>>

    fun select() {
        inventories = inventoryDAO.select()
    }

    fun filter(query: String?) {
        inventories = inventoryDAO.filter(query)
    }

    fun filter(startDate: Date, finalDate: Date) {
        inventories = inventoryDAO.filter(startDate, finalDate)
    }

    @WorkerThread
    suspend fun insert(inventory: Inventory) {
        inventoryDAO.insert(inventory)
    }

    @WorkerThread
    suspend fun update(inventory: Inventory) {
        inventoryDAO.update(inventory)
    }

    @WorkerThread
    suspend fun delete(inventory: Inventory) {
        inventoryDAO.delete(inventory)
    }

    @WorkerThread
    suspend fun selectLastInsert() = inventoryDAO.selectLastInsert()
}