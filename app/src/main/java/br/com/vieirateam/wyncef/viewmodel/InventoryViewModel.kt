package br.com.vieirateam.wyncef.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import br.com.vieirateam.wyncef.database.WyncefDatabase
import br.com.vieirateam.wyncef.entity.Inventory
import br.com.vieirateam.wyncef.interfaces.ViewModelScope
import br.com.vieirateam.wyncef.repository.InventoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class InventoryViewModel(application: Application) : AndroidViewModel(application), ViewModelScope<Inventory> {

    private var inventoryDAO = WyncefDatabase.getDatabase(viewModelScope).inventoryDAO()
    private var inventoryRepository = InventoryRepository(inventoryDAO)
    lateinit var inventories: LiveData<List<Inventory>>

    override fun getScope(): CoroutineScope = viewModelScope

    override fun select() {
        inventoryRepository.select()
        inventories = inventoryRepository.inventories
    }

    override fun insert(item: Inventory) = viewModelScope.launch(Dispatchers.IO) {
        inventoryRepository.insert(item)
    }

    override fun update(item: Inventory) = viewModelScope.launch(Dispatchers.IO) {
        inventoryRepository.update(item)
    }

    override fun delete(item: Inventory) = viewModelScope.launch(Dispatchers.IO) {
        inventoryRepository.delete(item)
    }

    suspend fun selectLastInsert() = inventoryRepository.selectLastInsert()

    fun filter(query: String?) {
        inventoryRepository.filter("%$query%")
        inventories = inventoryRepository.inventories
    }

    fun filter(startDate: Date, finalDate: Date) {
        inventoryRepository.filter(startDate, finalDate)
        inventories = inventoryRepository.inventories
    }
}