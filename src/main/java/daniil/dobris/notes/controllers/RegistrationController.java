package daniil.dobris.notes.controllers;

import daniil.dobris.notes.entities.Role;
import daniil.dobris.notes.entities.User;
import daniil.dobris.notes.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        User newUser = new User();
        newUser.setRole(Role.USER);
        model.addAttribute("userForm", newUser);
        return "registration";
    }
    @PostMapping("/registration")
    public String addUser(@ModelAttribute("userForm") @Valid User userForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())) {
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "registration";
        }
        userForm.setRole(Role.USER);
        if (!userService.saveUser(userForm)) {
            model.addAttribute("usernameError", "Username already exists");
            return "registration";
        }
        return "redirect:/login";
    }
}
