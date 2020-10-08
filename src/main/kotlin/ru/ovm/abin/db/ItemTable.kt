package ru.ovm.abin.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import ru.ovm.abin.Utils
import ru.ovm.abin.db.pojo.Category
import ru.ovm.abin.db.pojo.Duplicate
import ru.ovm.abin.db.utils.DatabaseFactory

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
    val duplicates: List<Duplicate>? = null,
    var firstInstance: Item? = null,
    var deleted: Boolean = false
) {

    fun addComment(comment: String) {
        this.comments += comment + "\n"
    }
}

data class FrontItem(
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
    val duplicates: List<Duplicate>? = null
)

internal object ItemTable : IntIdTable("item") {
    val seller = reference("SELLER_VK_ID", SellerTable).index()
    val vkAlbum = reference("VK_ALBUM_VK_ID", VkAlbumTable).index()
    val category = customEnumeration("category", null, { Category.valueOf(it as String) }, { it.name })
    val photo_id = integer("photo_id")
    val url = varchar("url", 255)
    val photo_src = varchar("photo_src", 255)
    val description = text("description") //todo clob
    val comments = text("comments")
    val info = text("info").index()
    val timestamp = integer("timestamp").index()
    val vkGroup = reference("VK_GROUP_VK_ID", VkGroupTable).index()
    val firstInstance = reference("FIRST_INSTANCE_ID", ItemTable).nullable().index()
    val deleted = bool("deleted").index()
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
    val sellerId by ItemTable.seller

    fun toModel(): Item {
        return Item(
            id.value, seller.toModel(), vkAlbum.toModel(), category, photo_id, url, photo_src,
            description, comments, info, timestamp, vkGroup.toModel(), duplicates.map { it.toModelAsDuplicate() },
            firstInstance?.toModel(), deleted
        )
    }

    fun toModel2(): FrontItem {
        return FrontItem(
            id.value, seller.toModel(), vkAlbum.toModel(), category, photo_id, url, photo_src,
            description, comments, info, timestamp, vkGroup.toModel(), duplicates.map { it.toModelAsDuplicate() }
        )
    }

    fun toModelAsDuplicate(): Duplicate {
        return Duplicate(vkGroup.vkId.value, url)
    }
}

suspend fun findItemsBySellerAndDeletedIsOrderByTimestampAsc(seller: Seller, deleted: Boolean): List<Item> = DatabaseFactory.dbQuery {
    ItemDao.find { (ItemTable.seller eq seller.vkId) and (ItemTable.deleted eq deleted) }
        .sortedBy { it.timestamp }
        .toList()
        .map { it.toModel() }
}

suspend fun getActiveItemsCount(): Long = DatabaseFactory.dbQuery {
    ItemDao.find { ItemTable.deleted eq false }.count()
}

suspend fun getAllItems(): List<Item> = DatabaseFactory.dbQuery { ItemDao.all().toList().map { it.toModel() } }

suspend fun get5ItemsAll(): List<Item> = DatabaseFactory.dbQuery { ItemDao.all().limit(5, 0).toList().map { it.toModel() } }

suspend fun get5Items(): List<Item> = DatabaseFactory.dbQuery {
    ItemDao.find { ItemTable.deleted eq false }
        .limit(5)
        .toList()
        .map { it.toModel() }
}

suspend fun getItem(id: Int): Item? = DatabaseFactory.dbQuery { ItemDao.findById(id)?.toModel() }

suspend fun deleteItem(id: Int) = DatabaseFactory.dbQuery { ItemDao.findById(id)?.delete() }


suspend fun getPageOfItems(
    query: String = "",
    category: Category = Category.ALL,
    seller: Int? = null,
    page: Int = 1,
    count: Int = 50,
    start_time: Int = 0,
    deleted: Boolean = false
): List<FrontItem> = DatabaseFactory.dbQuery {
    ItemDao.find {
        var op = (ItemTable.deleted eq deleted) and
                (ItemTable.timestamp greaterEq start_time) and
                (ItemTable.firstInstance.isNull())

        if (category != Category.ALL) op = op and (ItemTable.category eq category)
        if (seller != null) op = op and (ItemTable.seller eq seller)
        if (!query.isBlank()) op = op and (ItemTable.info like "%${query.trim { it <= ' ' }}%")

        op
    }
        .orderBy(ItemTable.timestamp to SortOrder.DESC)
        .limit(count, ((page - 1) * count).toLong())
        .toList()
        .map { it.toModel2() }
}
suspend fun getPageOfItems2(
    query: String = "",
    category: Category = Category.ALL,
    seller: Int? = null,
    page: Int = 1,
    count: Int = 50,
    start_time: Int = 0,
    deleted: Boolean = false
): List<FrontItem> = DatabaseFactory.dbQuery {

    ItemTable.select { var op = (ItemTable.deleted eq deleted) and
            (ItemTable.timestamp greaterEq start_time) and
            (ItemTable.firstInstance.isNull())

        if (category != Category.ALL) op = op and (ItemTable.category eq category)
        if (seller != null) op = op and (ItemTable.seller eq seller)
        if (!query.isBlank()) op = op and (ItemTable.info like "%${query.trim { it <= ' ' }}%")

        op }
        .orderBy(ItemTable.timestamp to SortOrder.DESC)
        .limit(count, ((page - 1) * count).toLong())
        .toList()
        .map { ItemDao.wrapRow(it) }
        .map { it.toModel2() }


}

