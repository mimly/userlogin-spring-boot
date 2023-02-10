package mimly.authentication.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j(topic = "** Routing Middleware **")
public class RoutingMiddleware extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).forEach(log::error);
        String uri = httpServletRequest.getRequestURI();
        if (authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch("ROLE_USER"::equals)
                && !uri.matches("(/|/ws/v1(/.*)?|/stomp\\.min\\.js)")) {
            httpServletResponse.sendRedirect("/");
            return;
        }

        chain.doFilter(request, response);
    }
}
