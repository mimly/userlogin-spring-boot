package mimly.userlogin.config;

import mimly.userlogin.controller.RoutingMiddleware;
import mimly.userlogin.controller.XSSFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (activeProfile.equalsIgnoreCase("test")) {
            http.csrf().disable();
        }

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .invalidSessionUrl("/login?error=Session expired");

        http.authorizeRequests()
                .antMatchers("/actuator/**").hasRole("ADMIN")
                .antMatchers("/login").anonymous()
                .antMatchers("/registration").anonymous()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .failureUrl("/login?error=Bad credentials")
                .defaultSuccessUrl("/", true)
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?success=Signed out successfully")
                .deleteCookies("SESSION")
                .and()
                .addFilterBefore(XSSFilter(), WebAsyncManagerIntegrationFilter.class)
                .addFilterBefore(commonsRequestLoggingFilter(), HeaderWriterFilter.class)
                .addFilterAfter(routingMiddleware(), SessionManagementFilter.class);
    }

    @Bean
    public XSSFilter XSSFilter() {
        return new XSSFilter();
    }

    @Bean
    public CommonsRequestLoggingFilter commonsRequestLoggingFilter() {
        CommonsRequestLoggingFilter commonsRequestLoggingFilter = new CommonsRequestLoggingFilter();
        commonsRequestLoggingFilter.setIncludeClientInfo(true);
        commonsRequestLoggingFilter.setIncludeQueryString(true);
        commonsRequestLoggingFilter.setIncludePayload(true);
        commonsRequestLoggingFilter.setMaxPayloadLength(256);
        commonsRequestLoggingFilter.setIncludeHeaders(false);
        return commonsRequestLoggingFilter;
    }

    @Bean
    public RoutingMiddleware routingMiddleware() {
        return new RoutingMiddleware();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jdbcUserDetailsManager());
        JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder> conf =
                new JdbcUserDetailsManagerConfigurer<>(jdbcUserDetailsManager());
        auth.apply(conf);
        conf.dataSource(dataSource)
                .withDefaultSchema().passwordEncoder(passwordEncoder())
                .withUser("mimly")
                .password(passwordEncoder().encode("mimly"))
                .roles("ADMIN");
    }

    @Autowired
    private DataSource dataSource;

    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager() {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}