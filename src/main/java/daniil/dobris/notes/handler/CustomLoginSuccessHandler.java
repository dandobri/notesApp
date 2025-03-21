package daniil.dobris.notes.handler;

import daniil.dobris.notes.entities.User;
import daniil.dobris.notes.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final UserService userService;

    public CustomLoginSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<User> userByUsername = userService.findUserByUsername(username);
        if (userByUsername.isPresent()) {
            response.sendRedirect("/notes?userId=" + userByUsername.get().getId());
        } else {
            response.sendRedirect("/login?error");
        }
    }
}
