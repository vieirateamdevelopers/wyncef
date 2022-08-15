package br.com.vieirateam.wyncef.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import br.com.vieirateam.wyncef.database.dao.DeviceDAO
import br.com.vieirateam.wyncef.entity.Device
import java.util.Date

class DeviceRepository(private val deviceDAO: DeviceDAO) {

    lateinit var devices: LiveData<List<Device>>

    fun select() {
        devices = deviceDAO.select()
    }

    fun filter(query: String?) {
        devices = deviceDAO.filter(query)
    }

    fun filter(startDate: Date, finalDate: Date) {
        devices = deviceDAO.filter(startDate, finalDate)
    }

    @WorkerThread
    suspend fun insert(device: Device) {
        deviceDAO.insert(device)
    }

    @WorkerThread
    suspend fun update(device: Device) {
        deviceDAO.update(device)
    }

    @WorkerThread
    suspend fun delete(device: Device) {
        deviceDAO.delete(device)
    }

    @WorkerThread
    suspend fun selectDevice(query: String) = deviceDAO.selectDevice(query)
}