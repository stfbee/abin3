package ru.ovm.abin.db.utils

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import ru.ovm.abin.db.*

object DatabaseFactory {
    fun init() {
        Database.connect(hikari())
//        transaction {
//            ItemTable.innerJoin(SellerTable).innerJoin(VkGroupTable).innerJoin(VkAlbumTable).selectAll().toList().map {
//                VkAlbumDao.wrapRow(it)
//                VkGroupDao.wrapRow(it)
//                SellerDao.wrapRow(it)
//                ItemDao.wrapRow(it)
//            }
//                .forEach { println(it) }
//        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.username = "sa"
        config.password = ""
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:file:./abin.db;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE"
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction { block() }
}
