package daniil.dobris.notes.config;

import daniil.dobris.notes.handler.CustomLoginSuccessHandler;
import daniil.dobris.notes.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static daniil.dobris.notes.entities.Role.ADMIN;
import static daniil.dobris.notes.entities.Role.USER;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig extends WebConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    @Autowired
    private CustomLoginSuccessHandler successHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/registration").permitAll()
                        .requestMatchers("/notes/**").hasAnyAuthority(ADMIN.getAuthority(), USER.getAuthority())
                        .requestMatchers("/admin/**").hasRole(ADMIN.getAuthority())
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID"));
        return http.build();
    }
    /*@Autowired
    private UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/login", "/registration").permitAll()
                                .requestMatchers("/notes/**").authenticated()
                                .anyRequest().denyAll()
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")
                                .defaultSuccessUrl("/notes", true)
                                .permitAll()
                )
                .logout(logout ->
                    logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(exceptionHandling ->
                    exceptionHandling
                        .accessDeniedPage("/accessDenied")
                ).build();
                //.userDetailsService(userService).build();
    }*/
}
