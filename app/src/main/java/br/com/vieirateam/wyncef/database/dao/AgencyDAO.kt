package br.com.vieirateam.wyncef.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import br.com.vieirateam.wyncef.entity.Agency
import java.util.Date

@Dao
interface AgencyDAO : BaseDAO<Agency> {

    @Query("SELECT * FROM agency WHERE agency_name == :query OR agency_cgc ==:query LIMIT 1")
    suspend fun selectAgency(query: String): Agency?

    @Query("SELECT * FROM agency ORDER BY agency_name ASC")
    fun select(): LiveData<List<Agency>>

    @Query("SELECT * FROM agency WHERE agency_cgc LIKE :query OR agency_name LIKE :query ORDER BY agency_name ASC")
    fun filter(query: String?): LiveData<List<Agency>>

    @Query("SELECT * FROM agency WHERE agency_date BETWEEN :startDate AND :finalDate ORDER BY agency_date ASC")
    fun filter(startDate: Date, finalDate: Date): LiveData<List<Agency>>
}