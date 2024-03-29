package mimly.authentication.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
@Slf4j(topic = "** Login **")
public class LoginController {

    @GetMapping
    public String get() {
        return "login";
    }
}
