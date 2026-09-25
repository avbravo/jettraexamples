import java.io.InputStream;
public class TestResource {
    public static void main(String[] args) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("static/font-awesome/css/all.min.css");
        System.out.println("Resource found: " + (is != null));
    }
}
