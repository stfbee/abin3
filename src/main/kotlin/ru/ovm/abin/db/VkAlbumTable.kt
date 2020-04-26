package ru.ovm.abin.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import ru.ovm.abin.db.pojo.Category
import ru.ovm.abin.db.pojo.Status
import ru.ovm.abin.db.utils.DatabaseFactory

data class VkAlbum(
    val vkId: Int,
    val category: Category,
    val name: String,
    val deleted: Boolean,
    val status: Status,
    val groupId: Int
)

data class VkAlbumWithGroup(
    val vkId: Int,
    val category: Category,
    val name: String,
    val deleted: Boolean,
    val status: Status,
    val vkGroup: VkGroup
)

internal object VkAlbumTable : IntIdTable("vk_album", "vk_id") {
    val category = customEnumeration("category", null, { Category.valueOf(it as String) }, { it.name })
    val name = varchar("name", 255)
    val deleted = bool("deleted")
    val status = customEnumeration("status", null, { Status.valueOf(it as String) }, { it.name })
    val vkGroup = reference("VK_GROUP_VK_ID", VkGroupTable)
}

internal class VkAlbumDao(val vkId: EntityID<Int>) : IntEntity(vkId) {
    companion object : IntEntityClass<VkAlbumDao>(VkAlbumTable)

    var category by VkAlbumTable.category
    var name by VkAlbumTable.name
    var deleted by VkAlbumTable.deleted
    var status by VkAlbumTable.status
    var vkGroup by VkGroupDao referencedOn VkAlbumTable.vkGroup

    fun toModel(): VkAlbum {
        return VkAlbum(vkId.value, category, name, deleted, status, vkGroup.id.value)
    }

    fun toModelWithGroup(): VkAlbumWithGroup {
        return VkAlbumWithGroup(vkId.value, category, name, deleted, status, vkGroup.toModel())
    }
}

suspend fun get5Albums(): List<VkAlbum> = DatabaseFactory.dbQuery {
    VkAlbumDao.all()
        .limit(5)
        .toList()
        .map { it.toModel() }
}

suspend fun get5AlbumsWithGroups(): List<VkAlbumWithGroup> = DatabaseFactory.dbQuery {
    VkAlbumDao.all()
        .limit(5)
        .toList()
        .map { it.toModelWithGroup() }
}

suspend fun getActiveAlbumsCount(): Long = DatabaseFactory.dbQuery {
    VkAlbumDao.find { VkAlbumTable.deleted eq false }.count()
}

