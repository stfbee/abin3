package ru.ovm.abin

import com.fasterxml.jackson.databind.SerializationFeature
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.ServiceActor
import com.vk.api.sdk.client.actors.UserActor
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.routing.get
import io.ktor.routing.routing
import ru.ovm.abin.db.*
import ru.ovm.abin.db.pojo.Category
import ru.ovm.abin.db.utils.DatabaseFactory
import ru.ovm.abin.returns.Info
import java.io.File

object App {
    lateinit var vk: VkApiClient
    lateinit var actorS: ServiceActor
    lateinit var actorU: UserActor

    var admin: String? = ""
    var group_id = 0
    var version = "3.0.0"

    /**
     * Минимальный размер описания, при котором он начинает считаться описанием.
     * Нужно, чтобы отсечь пустые описания или вроде "в первом комментарии" и тд
     */
    var min_desc_size = 25
}

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)

    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
        }
    }

    DatabaseFactory.init()

    routing {
        get("/api/sellers/") { call.respond(get5Sllers()) }
        get("/api/groups/") { call.respond(get5GroupsWithAlbums()) }
        get("/api/albums/") { call.respond(get5Albums()) }
        get("/api/items/") {
            val parameters = call.parameters
            val query = parameters["query"] ?: ""
            val category = Category.valueOf(parameters["category"]?.toUpperCase() ?: "ALL")
            val seller = parameters["seller"]?.toIntOrNull()
            val page = (parameters["page"] ?: "1").toInt()
            val count = (parameters["count"] ?: "50").toInt()
            val start_time = (parameters["start_time"] ?: "0").toInt()
            val deleted = (parameters["deleted"] ?: "false").toBoolean()
            call.respond(getPageOfItems(query, category, seller, page, count, start_time, deleted))
        }
        get("/api/items2/") {
            val parameters = call.parameters
            val query = parameters["query"] ?: ""
            val category = Category.valueOf(parameters["category"]?.toUpperCase() ?: "ALL")
            val seller = parameters["seller"]?.toIntOrNull()
            val page = (parameters["page"] ?: "1").toInt()
            val count = (parameters["count"] ?: "50").toInt()
            val start_time = (parameters["start_time"] ?: "0").toInt()
            val deleted = (parameters["deleted"] ?: "false").toBoolean()
            call.respond(getPageOfItems2(query, category, seller, page, count, start_time, deleted))
        }

        get("/api/items51/") { call.respond(get5ItemsAll()) }
        get("/api/items52/") { call.respond(get5Items()) }
        get("/api/draw/") {
            val pathname = "image.png"
            VkHeaderGenerator().drawText(getActiveGroupsCount().toString(), getActiveAlbumsCount().toString(), getActiveItemsCount().toString(), pathname)
            call.respondFile(File(pathname))
        }

        get("/api/info") {
            call.respond(Info(getActiveItemsCount(), getActiveGroupsCount(), getActiveAlbumsCount(), App.version))
        }
    }
}