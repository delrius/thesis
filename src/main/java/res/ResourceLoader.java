package res;

import java.io.InputStream;

public class ResourceLoader {

    public static InputStream getInputStreamForResource(String res) {
        return ResourceLoader.class.getResourceAsStream(res);
    }
}
