package ru.grishuchkov.cloudfilestorage.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String getHome(@AuthenticationPrincipal UserDetails userDetails) {

        return "home";
    }
}
