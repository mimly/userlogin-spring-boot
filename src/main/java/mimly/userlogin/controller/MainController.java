package mimly.userlogin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@Slf4j(topic = "** Main **")
public class MainController {

    @Component
    @RequestMapping("/")
    class MainErrorController implements ErrorController {

        @GetMapping("/error")
        public String get() {
            log.warn("404 Not Found");
            if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
                return "redirect:/index";
            }
            return "redirect:/login";
        }

        @Override
        public String getErrorPath() {
            return "/error";
        }
    }

    @GetMapping("/")
    public String get() {
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            return "redirect:/index";
        }
        return "redirect:/login";
    }

    @GetMapping("/index")
    public String getIndex() {
        return "index";
    }
}