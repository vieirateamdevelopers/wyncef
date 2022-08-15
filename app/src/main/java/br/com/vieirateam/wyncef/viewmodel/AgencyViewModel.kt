package br.com.vieirateam.wyncef.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import br.com.vieirateam.wyncef.database.WyncefDatabase
import br.com.vieirateam.wyncef.entity.Agency
import br.com.vieirateam.wyncef.interfaces.ViewModelScope
import br.com.vieirateam.wyncef.repository.AgencyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class AgencyViewModel(application: Application) : AndroidViewModel(application), ViewModelScope<Agency> {

    private var agencyDAO = WyncefDatabase.getDatabase(viewModelScope).agencyDAO()
    private var agencyRepository = AgencyRepository(agencyDAO)
    lateinit var agencies: LiveData<List<Agency>>

    override fun getScope(): CoroutineScope = viewModelScope

    override fun select() {
        agencyRepository.select()
        agencies = agencyRepository.agencies
    }

    override fun insert(item: Agency) = viewModelScope.launch(Dispatchers.IO) {
        agencyRepository.insert(item)
    }

    override fun update(item: Agency) = viewModelScope.launch(Dispatchers.IO) {
        agencyRepository.update(item)
    }

    override fun delete(item: Agency) = viewModelScope.launch(Dispatchers.IO) {
        agencyRepository.delete(item)
    }

    suspend fun selectAgency(query: String) = agencyRepository.selectAgency(query)

    fun filter(query: String?) {
        agencyRepository.filter("%$query%")
        agencies = agencyRepository.agencies
    }

    fun filter(startDate: Date, finalDate: Date) {
        agencyRepository.filter(startDate, finalDate)
        agencies = agencyRepository.agencies
    }
}