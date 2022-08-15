package br.com.vieirateam.wyncef.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object WyncefMigrate {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}