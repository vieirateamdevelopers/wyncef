package br.com.vieirateam.wyncef.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import br.com.vieirateam.wyncef.entity.Inventory
import java.util.Date

@Dao
interface InventoryDAO : BaseDAO<Inventory>{

    @Query("SELECT * FROM inventory ORDER BY inventory_id DESC")
    fun select(): LiveData<List<Inventory>>

    @Query("SELECT * FROM inventory ORDER BY inventory_id DESC LIMIT 1")
    suspend fun selectLastInsert(): Inventory?

    @Query("SELECT * FROM inventory WHERE inventory_agency_name LIKE :query OR inventory_agency_cgc LIKE :query ORDER BY inventory_id DESC")
    fun filter(query: String?): LiveData<List<Inventory>>

    @Query("SELECT * FROM inventory WHERE inventory_date BETWEEN :startDate AND :finalDate ORDER BY inventory_date ASC")
    fun filter(startDate: Date, finalDate: Date): LiveData<List<Inventory>>
}