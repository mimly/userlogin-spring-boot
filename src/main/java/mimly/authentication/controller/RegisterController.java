package mimly.authentication.controller;

import lombok.extern.slf4j.Slf4j;
import mimly.authentication.model.RegisterDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/register")
@Slf4j(topic = "** Register **")
public class RegisterController {

    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegisterController(@Qualifier("jdbcUserDetailsManager") UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String get() {
        return "register";
    }

    @PostMapping
    public String post(@Valid RegisterDTO registerDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            String error = errors.get(errors.size() - 1).getDefaultMessage();
            return "redirect:register?error=" + StringUtils.capitalize(error);
        }

        if (this.userDetailsManager.userExists(registerDTO.getUsername())) {
            return "redirect:register?error=User already exists";
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails user = new User(
                registerDTO.getUsername(),
                this.passwordEncoder.encode(registerDTO.getPassword()),
                authorities
        );
        this.userDetailsManager.createUser(user);
        log.info(this.userDetailsManager.loadUserByUsername(registerDTO.getUsername()).toString());
        return "redirect:register?success=New account created";
    }
}
