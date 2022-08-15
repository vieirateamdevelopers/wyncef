package br.com.vieirateam.wyncef.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import br.com.vieirateam.wyncef.database.WyncefDatabase
import br.com.vieirateam.wyncef.entity.Device
import br.com.vieirateam.wyncef.interfaces.ViewModelScope
import br.com.vieirateam.wyncef.repository.DeviceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class DeviceViewModel(application: Application) : AndroidViewModel(application), ViewModelScope<Device> {

    private var deviceDAO = WyncefDatabase.getDatabase(viewModelScope).deviceDAO()
    private var deviceRepository = DeviceRepository(deviceDAO)
    lateinit var devices: LiveData<List<Device>>

    override fun getScope(): CoroutineScope = viewModelScope

    override fun select() {
        deviceRepository.select()
        devices = deviceRepository.devices
    }

    override fun insert(item: Device) = viewModelScope.launch(Dispatchers.IO) {
        deviceRepository.insert(item)
    }

    override fun update(item: Device) = viewModelScope.launch(Dispatchers.IO) {
        deviceRepository.update(item)
    }

    override fun delete(item: Device) = viewModelScope.launch(Dispatchers.IO) {
        deviceRepository.delete(item)
    }

    suspend fun selectDevice(query: String) = deviceRepository.selectDevice(query)

    fun filter(query: String?) {
        deviceRepository.filter("%$query%")
        devices = deviceRepository.devices
    }

    fun filter(startDate: Date, finalDate: Date) {
        deviceRepository.filter(startDate, finalDate)
        devices = deviceRepository.devices
    }
}