package db

import Utils
import db.pojo.Category
import db.utils.DatabaseFactory
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

data class Item(
    val id: Int,
    var seller: Seller,
    var vkAlbum: VkAlbum,
    var category: Category,
    var photo_id: Int,
    var url: String = Utils.getPhotoLink(vkAlbum, photo_id),
    var photo_src: String,
    var description: String = "",
    var comments: String = "",
    var info: String = "",
    var timestamp: Int = 0,
    var vkGroup: VkGroup? = null,
    val duplicates: List<Item>? = null,
    var firstInstance: Item? = null,
    var deleted: Boolean = false
) {

    fun addComment(comment: String) {
        this.comments += comment + "\n"
    }
}

internal object ItemTable : IntIdTable("item") {
    val seller = reference("SELLER_VK_ID", SellerTable)
    val vkAlbum = reference("VK_ALBUM_VK_ID", VkAlbumTable)
    val category = customEnumeration("category", null, { Category.valueOf(it as String) }, { it.name })
    val photo_id = integer("photo_id")
    val url = varchar("url", 255)
    val photo_src = varchar("photo_src", 255)
    val description = text("description") //todo clob
    val comments = text("comments")
    val info = text("info")
    val timestamp = integer("timestamp")
    val vkGroup = reference("VK_GROUP_VK_ID", VkGroupTable)
    val firstInstance = reference("FIRST_INSTANCE_ID", ItemTable).nullable()
    val deleted = bool("deleted")
}

internal class ItemDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ItemDao>(ItemTable)

    val seller by SellerDao referencedOn ItemTable.seller
    val vkAlbum by VkAlbumDao referencedOn ItemTable.vkAlbum
    val category by ItemTable.category
    val photo_id by ItemTable.photo_id
    val url by ItemTable.url
    val photo_src by ItemTable.photo_src
    val description by ItemTable.description
    val comments by ItemTable.comments
    val info by ItemTable.info
    val timestamp by ItemTable.timestamp
    val vkGroup by VkGroupDao referencedOn ItemTable.vkGroup
    val duplicates by ItemDao optionalReferrersOn ItemTable.firstInstance
    val firstInstance by ItemDao optionalReferencedOn ItemTable.firstInstance
    val deleted by ItemTable.deleted

    fun toModel(): Item {
        return Item(
            id.value, seller.toModel(), vkAlbum.toModel(), category, photo_id, url, photo_src,
            description, comments, info, timestamp, vkGroup.toModel(), duplicates.map { it.toModel() },
            firstInstance?.toModel(), deleted
        )
    }
}

suspend fun getAllItems(): List<Item> = DatabaseFactory.dbQuery { ItemDao.all().toList().map { it.toModel() } }

suspend fun get5Items(): List<Item> = DatabaseFactory.dbQuery { ItemDao.all().limit(5, 0).toList().map { it.toModel() } }

suspend fun getItem(id: Int): Item? = DatabaseFactory.dbQuery { ItemDao.findById(id)?.toModel() }

suspend fun deleteItem(id: Int) = DatabaseFactory.dbQuery { ItemDao.findById(id)?.delete() }



