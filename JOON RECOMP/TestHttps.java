import java.net.URI;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.HttpClient;

public class TestHttps {
    public static void main(String[] args) throws Exception {
        for (String url : args) {
            System.out.println("Testing: " + url);
            try {
                HttpPost post = new HttpPost(url);
                HttpClient client = HttpClients.createDefault();
                var resp = client.execute(post);
                System.out.println("  Status: " + resp.getStatusLine().getStatusCode());
            } catch (Exception e) {
                System.out.println("  FAIL: " + e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }
}
