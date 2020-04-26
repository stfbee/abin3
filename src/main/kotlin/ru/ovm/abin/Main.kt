package ru.ovm.abin

import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.ServiceActor
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.apache.http.impl.client.HttpClientBuilder

fun main(args: Array<String>) {
    val transportClient = HttpTransportClient.getInstance()
    try {
        val httpClientField = HttpTransportClient::class.java.getDeclaredField("httpClient")
        httpClientField.isAccessible = true
        httpClientField.set(null, HttpClientBuilder.create().useSystemProperties().build())
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }

    App.vk = VkApiClient(transportClient)

    App.actorS = ServiceActor(6259405, System.getenv("token_s"))
    App.actorU = UserActor(8043960, System.getenv("token_u"))

    App.admin = System.getenv("admin_password") ?: ""

    val cover_group = System.getenv("cover_group")
    if (cover_group != null && !cover_group.isEmpty()) {
        App.group_id = Integer.valueOf(cover_group)
    }

    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}