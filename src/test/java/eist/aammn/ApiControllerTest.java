package eist.aammn;

import static org.junit.jupiter.api.Assertions.*;

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
@WebMvcTest(ApiController.class)
@ContextConfiguration(classes = ApiController.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCuisines() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/cuisines/");
        var resp = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertEquals(200, resp.getStatus());
    }

    @Test
    public void testApi404() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/cuisines/test");
        var resp = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertEquals(404, resp.getStatus());
    }
}
