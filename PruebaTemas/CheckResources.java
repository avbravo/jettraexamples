import java.net.URL;
import java.util.Enumeration;

public class CheckResources {
    public static void main(String[] args) throws Exception {
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources("META-INF/jettra/page.classes");
        while (resources.hasMoreElements()) {
            System.out.println(resources.nextElement());
        }
    }
}
