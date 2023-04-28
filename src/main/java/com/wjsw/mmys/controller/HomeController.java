package com.wjsw.mmys.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String homepage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails.getUsername() != null) {
            String username = userDetails.getUsername();
            model.addAttribute("username", username);
            return "index";
        } else {
            model.addAttribute("username","방문자");
            return "index";
        }
    }
}
