package ru.grishuchkov.cloudfilestorage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.grishuchkov.cloudfilestorage.dto.UserRegistration;
import ru.grishuchkov.cloudfilestorage.service.UserService;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class UserController {

    private final UserService userService;

    @GetMapping({"/", "/login"})
    public String getLoginPage(@AuthenticationPrincipal Principal principal) {

        return "login";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model,
                                  @AuthenticationPrincipal Principal principal) {

        model.addAttribute("UserRegistration", new UserRegistration());

        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("UserRegistration") @Valid UserRegistration userRegistration,
                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        userService.registration(userRegistration);

        return "redirect:login";
    }
}
