package com.example.projectboard.interfaces.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class AuthViewController {

    @GetMapping("/auth/login")
    public String loginPage() {
        return "sign-in";
    }
}
