package mimly.userlogin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/")
@Slf4j(topic = "** Home **")
public class HomeController {

    @GetMapping
    public String getIndex(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "index";
    }
}