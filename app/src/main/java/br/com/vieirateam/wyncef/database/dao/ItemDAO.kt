package br.com.vieirateam.wyncef.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import br.com.vieirateam.wyncef.entity.Item
import java.util.Date

@Dao
interface ItemDAO : BaseDAO<Item> {

    @Query("DELETE FROM baseItem WHERE item_inventory_id == :inventoryID")
    suspend fun deleteItems(inventoryID: Long)

    @Query("SELECT * FROM baseItem WHERE item_inventory_id == :inventoryID AND item_device_serial_number == :serialNumber LIMIT 1")
    suspend fun selectItem(inventoryID: Long, serialNumber: String): Item?

    @Query("SELECT * FROM baseItem WHERE item_device_serial_number == :serialNumber LIMIT 1")
    suspend fun selectItem(serialNumber: String): Item?

    @Query("SELECT * FROM baseItem WHERE item_inventory_id == :inventoryID ORDER BY item_order ASC")
    fun select(inventoryID: Long): LiveData<List<Item>>

    @Query("SELECT * FROM baseItem WHERE item_inventory_id == :inventoryID AND item_device_serial_number LIKE :query OR item_device_logical_name LIKE :query OR item_device_patrimony LIKE :query OR item_device_category_name LIKE :query OR item_device_category_initials LIKE :query  ORDER BY item_order ASC")
    fun filter(inventoryID: Long, query: String?): LiveData<List<Item>>

    @Query("SELECT * FROM baseItem WHERE item_inventory_id == :inventoryID AND item_device_date BETWEEN :startDate AND :finalDate ORDER BY item_order ASC")
    fun filter(inventoryID: Long, startDate: Date, finalDate: Date): LiveData<List<Item>>
}