package bierstubeReservationTool.controller;

import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import bierstubeReservationTool.util.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class FrontendController {
    private final byte[] html;
    private final Map<String, ResponseEntity<byte[]>> assetCache;

    public FrontendController() {
        html = Objects.requireNonNull(ResourceLoader.loadResource("/static/index.html"), "cannot find /static/index.html resource");

        assetCache = new HashMap<>();
    }

    /**
     * Serve the same html document for each URL that does not start with <code>/api</code> or <code>/assets</code>.
     * Vue does the routing (deciding on what to show on <code>/</code> vs <code>/restaurants</code>).
     */
    @GetMapping(path = "/{path:^(?!api|assets).*$}", produces = MediaType.TEXT_HTML_VALUE)
    public byte[] getPage(@PathVariable String path) {
        return html;
    }

    /**
     * Same as <code>getPage</code> but for sub paths.
     */
    @GetMapping(path = "/{path:^(?!api|assets).*$}/**", produces = MediaType.TEXT_HTML_VALUE)
    public byte[] getSubPage(@PathVariable String path) {
        return getPage(path);
    }

    /**
     * Serve asset.
     */
    @GetMapping("/assets/{path}")
    public ResponseEntity<byte[]> getAsset(@PathVariable String path) {
        // this is very important so no one can request something like `/assets/../../`
        // which could be a security vulnerability
        if (path.contains("/")) {
            return ResponseEntity.notFound().build();
        }

        ResponseEntity<byte[]> response;
        synchronized (assetCache) {
            response = assetCache.computeIfAbsent(path, p -> {
                var asset = ResourceLoader.loadResource("/static/assets/" + path);
                if (asset == null) {
                    return null;
                }

                MediaType mt;
                var ct = URLConnection.guessContentTypeFromName(path);
                if (ct == null) {
                    mt = MediaType.APPLICATION_OCTET_STREAM;
                } else {
                    mt = MediaType.parseMediaType(ct);
                }

                return ResponseEntity.ok().contentType(mt).body(asset);
            });
        }

        return Objects.requireNonNullElseGet(
                response, () -> ResponseEntity.notFound().build());
    }
}
