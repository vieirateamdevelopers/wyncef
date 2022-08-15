package br.com.vieirateam.wyncef.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import br.com.vieirateam.wyncef.entity.Device
import java.util.Date

@Dao
interface DeviceDAO : BaseDAO<Device> {

    @Query("SELECT * FROM device WHERE device_serial_number == :query OR device_patrimony == :query LIMIT 1")
    suspend fun selectDevice(query: String): Device?

    @Query("SELECT * FROM device ORDER BY device_id DESC")
    fun select(): LiveData<List<Device>>

    @Query("SELECT * FROM device WHERE device_agency_name LIKE :query OR device_agency_cgc LIKE :query OR device_serial_number LIKE :query OR device_logical_name LIKE :query OR device_patrimony LIKE :query OR device_category_name LIKE :query OR device_category_initials LIKE :query ORDER BY device_id DESC")
    fun filter(query: String?): LiveData<List<Device>>

    @Query("SELECT * FROM device WHERE device_date BETWEEN :startDate AND :finalDate ORDER BY device_date ASC")
    fun filter(startDate: Date, finalDate: Date): LiveData<List<Device>>
}