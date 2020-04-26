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
import io.ktor.routing.get
import io.ktor.routing.routing
import ru.ovm.abin.db.get5Albums
import ru.ovm.abin.db.get5GroupsWithAlbums
import ru.ovm.abin.db.get5Items
import ru.ovm.abin.db.get5Sllers
import ru.ovm.abin.db.utils.DatabaseFactory

object App {
    lateinit var vk: VkApiClient
    lateinit var actorS: ServiceActor
    lateinit var actorU: UserActor

    var admin: String? = ""
    var group_id = 0
    var version = "2.1.0"

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
        get("/api/items/") { call.respond(get5Items()) }
    }
}