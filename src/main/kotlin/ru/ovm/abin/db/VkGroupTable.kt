package ru.ovm.abin.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import ru.ovm.abin.db.pojo.Status
import ru.ovm.abin.db.utils.DatabaseFactory

data class VkGroup(
    val vkId: Int,
    val name: String,
    val deleted: Boolean,
    val status: Status
)

data class VkGroupWithAlbums(
    val vkId: Int,
    val name: String,
    val deleted: Boolean,
    val status: Status,
    val albums: List<VkAlbum>
)

internal object VkGroupTable : IntIdTable("vk_group", "vk_id") {
    val name = varchar("name", 255)
    val deleted = bool("deleted")
    val status = customEnumeration("status", null, { Status.valueOf(it as String) }, { it.name })
}

internal class VkGroupDao(val vkId: EntityID<Int>) : IntEntity(vkId) {
    companion object : IntEntityClass<VkGroupDao>(VkGroupTable)

    var name by VkGroupTable.name
    var deleted by VkGroupTable.deleted
    var status by VkGroupTable.status
    val albums by VkAlbumDao referrersOn VkAlbumTable.vkGroup

    fun toModel(): VkGroup {
        return VkGroup(vkId.value, name, deleted, status)
    }

    fun toModelWithAlbums(): VkGroupWithAlbums {
        return VkGroupWithAlbums(vkId.value, name, deleted, status, albums.map { it.toModel() })
    }
}

suspend fun getAllGroups(): List<VkGroup> = DatabaseFactory.dbQuery {
    VkGroupDao.all()
        .toList()
        .map { it.toModel() }
}

suspend fun getAllGroupsWithAlbums(): List<VkGroupWithAlbums> = DatabaseFactory.dbQuery {
    VkGroupDao.all()
        .toList()
        .map { it.toModelWithAlbums() }
}

suspend fun getActiveGroupsCount(): Long = DatabaseFactory.dbQuery {
    VkGroupDao.find { VkGroupTable.deleted eq false }.count()
}
