package daniil.dobris.notes.controllers;

import daniil.dobris.notes.entities.User;
import daniil.dobris.notes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String checkUser(@RequestParam String username, @RequestParam String password, Model model) {
        Optional<User> userByUsername = userService.findUserByUsername(username);
        if (userByUsername.isEmpty()) {
            model.addAttribute("usernameError",
                    "Username not found, please try again or register");
            return "login";
        }
        if (!password.equals(userByUsername.get().getPassword())) {
            model.addAttribute("passwordError", "Wrong password");
            return "login";
        }
        return "redirect:/notes?userId=" + userByUsername.get().getId();
    }
}
