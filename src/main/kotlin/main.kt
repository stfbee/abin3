import com.fasterxml.jackson.databind.SerializationFeature
import db.get5Albums
import db.get5GroupsWithAlbums
import db.get5Items
import db.get5Sllers
import db.utils.DatabaseFactory
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)

    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
        }
    }

    DatabaseFactory.init()

    install(Routing) {
        seller()
    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}

fun Route.seller() {
    route("/sellers") {
        get("/") {
            call.respond(get5Sllers())
        }
    }
    route("/groups") {
        get("/") {
            call.respond(get5GroupsWithAlbums())
        }
    }
    route("/albums") {
        get("/") {
            call.respond(get5Albums())
        }
    }
    route("/items") {
        get("/") {
            call.respond(get5Items())
        }
    }
}