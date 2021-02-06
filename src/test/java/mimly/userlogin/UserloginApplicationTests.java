package mimly.userlogin;

import mimly.userlogin.controller.LoginController;
import mimly.userlogin.controller.HomeController;
import mimly.userlogin.controller.RegistrationController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserloginApplicationTests {

    @Autowired
    private LoginController loginController;

    @Autowired
    private HomeController homeController;

    @Autowired
    private RegistrationController registrationController;

    @Test
    void contextLoads() {
        assertThat(loginController).isNotNull();
        assertThat(homeController).isNotNull();
        assertThat(registrationController).isNotNull();
    }
}
