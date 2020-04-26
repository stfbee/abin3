import db.VkAlbum

/**
 * Created with love
 * by stfbe
 * on 04.04.2018
 */
object Utils {
    fun getPhotoLink(vkAlbum: VkAlbum, photo_id: Int): String {
        return "photo-" + vkAlbum.groupId + '_'.toString() + photo_id
    }

    /* */
    /**
     * Метод возвращает, если имеется, лот с таким же описанием (или первым комментом, если описание пустое) от того же продавца
     *
     * @param current_item текущий добавляемый лот
     * @return самый старый лот от того же продавца с таким же описанием
     *//*
    fun findDuplicate(itemRepository: ItemRepository, current_item: Item): Item? {
        val items = itemRepository.findBySellerAndDeletedIsOrderByTimestampAsc(current_item.seller, false)

        for (item in items) {
            if (item.url != current_item.url) {
                val description = item.description
                if (!description.isEmpty() && description.length >= App.min_desc_size) {
                    if (description.equals(current_item.description, ignoreCase = true)) {
                        return item
                    }
                } else {
                    val firstComment = item.comments
                    if (!firstComment.isEmpty() && firstComment.length >= App.min_desc_size) {
                        if (firstComment.equals(current_item.comments, ignoreCase = true)) {
                            return item
                        }
                    }
                }
            }
        }

        return null
    }*/
}
