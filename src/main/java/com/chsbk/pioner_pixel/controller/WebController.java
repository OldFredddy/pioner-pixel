package com.chsbk.pioner_pixel.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class WebController {


    @GetMapping("/dashboard")
    public String dash() { return "dashboard"; }
    @GetMapping("/")
    public String login() {
        return "auth_page";
    }


}
