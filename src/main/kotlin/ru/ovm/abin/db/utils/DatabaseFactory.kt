package ru.ovm.abin.db.utils

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object DatabaseFactory {
    fun init() {
        Database.connect(hikari())
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.username = "sa"
        config.password = ""
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:file:./abin.ru.ovm.abin.db;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE"
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction { block() }
}
