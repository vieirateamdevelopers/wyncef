package br.com.vieirateam.wyncef.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import br.com.vieirateam.wyncef.database.dao.AgencyDAO
import br.com.vieirateam.wyncef.entity.Agency
import java.util.Date

class AgencyRepository(private val agencyDAO: AgencyDAO) {

    lateinit var agencies: LiveData<List<Agency>>

    fun select() {
        agencies = agencyDAO.select()
    }

    fun filter(query: String?) {
        agencies = agencyDAO.filter(query)
    }

    fun filter(startDate: Date, finalDate: Date) {
        agencies = agencyDAO.filter(startDate, finalDate)
    }

    @WorkerThread
    suspend fun insert(agency: Agency) {
        agencyDAO.insert(agency)
    }

    @WorkerThread
    suspend fun update(agency: Agency) {
        agencyDAO.update(agency)
    }

    @WorkerThread
    suspend fun delete(agency: Agency) {
        agencyDAO.delete(agency)
    }

    @WorkerThread
    suspend fun selectAgency(query: String) = agencyDAO.selectAgency(query)
}