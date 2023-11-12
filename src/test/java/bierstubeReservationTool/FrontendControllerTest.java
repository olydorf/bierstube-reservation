package bierstubeReservationTool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import bierstubeReservationTool.controller.FrontendController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FrontendController.class)
@ContextConfiguration(classes = FrontendController.class)
public class FrontendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPage() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/");
        var resp = mockMvc.perform(requestBuilder).andReturn().getResponse();
        assertEquals(200, resp.getStatus());

        requestBuilder = MockMvcRequestBuilders.get("/restaurant/10");
        var resp2 = mockMvc.perform(requestBuilder).andReturn().getResponse();
        assertEquals(200, resp.getStatus());

        assertEquals(resp.getContentAsString(), resp2.getContentAsString());
    }

    @Test
    public void testAsset() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/assets/test-asset.css");
        var resp = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertEquals(200, resp.getStatus());
        assertTrue(Objects.requireNonNull(resp.getContentType()).startsWith("text/css"));
    }

    @Test
    public void testMissingAsset() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/assets/nonexistant-asset.css");
        var resp = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertEquals(404, resp.getStatus());
    }

    @Test
    public void testAssetSandboxSlashes() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/assets/../../application.properties");
        var resp = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertEquals(404, resp.getStatus());
    }

    @Test
    public void testAssetSandboxEscapedSlashes() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/assets/..%2F..%2Fapplication.properties");
        var resp = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertEquals(404, resp.getStatus());
    }
}
