package bierstubeReservationTool.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.springframework.lang.Nullable;

public class ResourceLoader {
    /**
     * Load a file relative to either <code>src/main/resources/</code> or <code>src/vue/dist</code>.
     */
    public static @Nullable byte[] loadResource(String path) {
        try {
            var i = ResourceLoader.class.getResourceAsStream(path);
            if (i == null) {
                return null;
            }
            return i.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("cannot load " + path + " resource", e);
        }
    }

    public static String mustLoadString(String path) throws FileNotFoundException {
        var bytes = loadResource(path);
        if (bytes == null)
            throw new FileNotFoundException("cannot find " + path + "resource");
        return new String(bytes);
    }
}
