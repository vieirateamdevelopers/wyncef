package br.com.vieirateam.wyncef.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import br.com.vieirateam.wyncef.database.WyncefDatabase
import br.com.vieirateam.wyncef.entity.Item
import br.com.vieirateam.wyncef.interfaces.ViewModelScope
import br.com.vieirateam.wyncef.repository.ItemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class ItemViewModel(application: Application) : AndroidViewModel(application), ViewModelScope<Item> {

    private var itemDAO = WyncefDatabase.getDatabase(viewModelScope).itemDAO()
    private var itemRepository = ItemRepository(itemDAO)
    lateinit var items: LiveData<List<Item>>

    override fun getScope(): CoroutineScope = viewModelScope

    override fun insert(item: Item) = viewModelScope.launch(Dispatchers.IO) {
        itemRepository.insert(item)
    }

    override fun update(item: Item) = viewModelScope.launch(Dispatchers.IO) {
        itemRepository.update(item)
    }

    override fun delete(item: Item) = viewModelScope.launch(Dispatchers.IO) {
        itemRepository.delete(item)
    }

    fun deleteItems(inventoryID: Long) = viewModelScope.launch(Dispatchers.IO) {
        itemRepository.deleteItems(inventoryID)
    }

    suspend fun selectItem(serialNumber: String) = itemRepository.selectItem(serialNumber)

    suspend fun selectItem(inventoryID: Long, serialNumber: String) = itemRepository.selectItem(inventoryID, serialNumber)

    fun select(inventoryID: Long) {
        itemRepository.select(inventoryID)
        items = itemRepository.items
    }

    fun filter(inventoryID: Long, query: String?) {
        itemRepository.filter(inventoryID, "%$query%")
        items = itemRepository.items
    }

    fun filter(inventoryID: Long, startDate: Date, finalDate: Date) {
        itemRepository.filter(inventoryID, startDate, finalDate)
        items = itemRepository.items
    }

    override fun select() {}
}