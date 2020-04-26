package ru.ovm.abin.tasks

import ru.ovm.abin.App
import ru.ovm.abin.App.actorU
import ru.ovm.abin.App.vk
import ru.ovm.abin.VkHeaderGenerator
import ru.ovm.abin.db.getActiveAlbumsCount
import ru.ovm.abin.db.getActiveGroupsCount
import ru.ovm.abin.db.getActiveItemsCount
import java.io.File

object Cover {
    suspend fun makeCover() {
        if (App.group_id == 0) return

        val pathname = "image.png"
        VkHeaderGenerator().drawText(getActiveGroupsCount().toString(), getActiveAlbumsCount().toString(), getActiveItemsCount().toString(), pathname)
        val ownerCoverPhotoUploadServer = vk.photos().getOwnerCoverPhotoUploadServer(actorU, App.group_id).cropX2(1590).cropY2(400).execute()
        val ownerCoverUploadResponse = vk.upload().photoOwnerCover(ownerCoverPhotoUploadServer.uploadUrl.toString(), File(pathname)).execute()
        vk.photos().saveOwnerCoverPhoto(actorU, ownerCoverUploadResponse.photo, ownerCoverUploadResponse.hash).execute()
    }
}