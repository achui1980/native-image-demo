package com.achui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FaviconController {

    @GetMapping("favicon.ico")
    @ResponseBody
    public void returnNoFavicon() {
        // Method is intentionally empty to return a 200 OK with no body
    }
}
