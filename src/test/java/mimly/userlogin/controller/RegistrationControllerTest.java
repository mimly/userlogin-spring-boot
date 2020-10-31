package mimly.userlogin.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private void testWithContentAndExpectSuccess(String content) throws Exception {
        this.mockMvc
                .perform(post("/registration")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(content)
                )
                .andExpect(ResultMatcher.matchAll(
                        status().isFound(), // 302
                        redirectedUrlPattern("/registration?success=*")
                ));
    }

    private void testWithContentAndExpectFailure(String content) throws Exception {
        this.mockMvc
                .perform(post("/registration")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(content)
                )
                .andExpect(ResultMatcher.matchAll(
                        status().isFound(), // 302
                        redirectedUrlPattern("/registration?error=*")
                ));
    }

    @Test
    public void successfulRegistration() throws Exception {
        testWithContentAndExpectSuccess("username=mimly1&password=mimly1&confirm=mimly1");
    }

    @Test
    public void userAlreadyExists() throws Exception {
        testWithContentAndExpectFailure("username=mimly&password=mimly1&confirm=mimly1");
    }

    @Test
    public void passwordsDoNotMatch() throws Exception {
        testWithContentAndExpectFailure("username=aaa&password=aaa&confirm=aaa1");
    }

    @Test
    public void passwordDoesNotContainAnyNumber() throws Exception {
        testWithContentAndExpectFailure("username=aaa&password=aaa&confirm=aaa");
    }

    @Test
    public void passwordSizeDoesNotMatch() throws Exception {
        testWithContentAndExpectFailure("username=aaa&password=a1&confirm=a1");
    }

    @Test
    public void passwordIsEmpty() throws Exception {
        testWithContentAndExpectFailure("username=aaa&password=&confirm=");
    }

    @Test
    public void passwordIsNull() throws Exception {
        testWithContentAndExpectFailure("username=aaa");
    }

    @Test
    public void usernameIsEmpty() throws Exception {
        testWithContentAndExpectFailure("username=&password=mimly1&confirm=mimly1");
    }

    @Test
    public void usernameIsNull() throws Exception {
        testWithContentAndExpectFailure("password=mimly1&confirm=mimly1");
    }
}
