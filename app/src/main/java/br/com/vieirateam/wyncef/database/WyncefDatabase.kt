package br.com.vieirateam.wyncef.database

import android.annotation.SuppressLint
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import br.com.vieirateam.wyncef.WyncefApplication
import br.com.vieirateam.wyncef.database.dao.*
import br.com.vieirateam.wyncef.entity.*
import br.com.vieirateam.wyncef.util.ConstantsUtil
import br.com.vieirateam.wyncef.util.Converters
import br.com.vieirateam.wyncef.util.CategoryUtil
import br.com.vieirateam.wyncef.util.UserPreferenceUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [Agency::class, Category::class, Device::class, Inventory::class, Item::class, Tag::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(Converters::class)

abstract class WyncefDatabase : RoomDatabase() {

    abstract fun agencyDAO(): AgencyDAO
    abstract fun categoryDAO(): CategoryDAO
    abstract fun deviceDAO(): DeviceDAO
    abstract fun inventoryDAO(): InventoryDAO
    abstract fun itemDAO(): ItemDAO
    abstract fun tagDAO(): TagDAO

    companion object {
        @Volatile
        private var INSTANCE: WyncefDatabase? = null

        fun getDatabase(scope: CoroutineScope): WyncefDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    WyncefApplication.getInstance().applicationContext,
                    WyncefDatabase::class.java,
                    ConstantsUtil.DATABASE_NAME)
                    .addMigrations(WyncefMigrate.MIGRATION_1_2)
                    .addCallback(WyncefCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class WyncefCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateCategory(database.categoryDAO())
                    }
                }
            }
        }

        @SuppressLint("DefaultLocale")
        suspend fun populateCategory(categoryDAO: CategoryDAO) {
            if (UserPreferenceUtil.firstRun) {
                for (item in CategoryUtil.getCategories()) {
                    if (item.name != "Outros") {
                        val category = Category(name = item.name.toUpperCase(), initials = item.initials.toUpperCase(), icon = item.icon)
                        categoryDAO.insert(category)
                    }
                }
                UserPreferenceUtil.firstRun = false
            }
        }
    }
}