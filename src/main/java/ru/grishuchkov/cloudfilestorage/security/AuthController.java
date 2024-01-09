package ru.grishuchkov.cloudfilestorage.security;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.grishuchkov.cloudfilestorage.dto.UserRegistration;
import ru.grishuchkov.cloudfilestorage.service.UserService;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public final class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String getLoginPage(@AuthenticationPrincipal UserDetails principal) {
        if(isAuthenticated(principal)){
            return "redirect:/home";
        }
        return "login";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model,
                                  @AuthenticationPrincipal UserDetails principal) {

        if(isAuthenticated(principal)){
            return "redirect:/home";
        }

        model.addAttribute("UserRegistration", new UserRegistration());
        return "register";
    }

    private boolean isAuthenticated(UserDetails principal){
        return principal != null;
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
