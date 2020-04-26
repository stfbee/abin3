package ru.ovm.abin.db

import ru.ovm.abin.db.utils.DatabaseFactory
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

data class Seller(
    val vkId: Int,
    val name: String,
    val city: String,
    val photoMax: String
)

internal object SellerTable : IntIdTable("seller", "vk_id") {
    val name = varchar("name", 50)
    val city = varchar("city", 50)
    val photoMax = varchar("photo_max", 255)
}

internal class SellerDao(val vkId: EntityID<Int>) : IntEntity(vkId) {
    companion object : IntEntityClass<SellerDao>(SellerTable)

    var name: String by SellerTable.name
    var city: String by SellerTable.city
    var photoMax: String by SellerTable.photoMax

    fun toModel(): Seller {
        return Seller(vkId.value, name, city, photoMax)
    }
}

suspend fun getAllSellers(): List<Seller> = DatabaseFactory.dbQuery { SellerDao.all().toList().map { it.toModel() } }

suspend fun get5Sllers(): List<Seller> = DatabaseFactory.dbQuery { SellerDao.all().limit(5, 0).toList().map { it.toModel() } }

suspend fun getSeller(id: Int): Seller? = DatabaseFactory.dbQuery { SellerDao.findById(id)?.toModel() }

suspend fun deleteSeller(id: Int) = DatabaseFactory.dbQuery { SellerDao.findById(id)?.delete() }



