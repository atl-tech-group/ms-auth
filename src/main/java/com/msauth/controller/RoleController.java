package com.msauth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/role")
public class RoleController {

    @GetMapping("/admin")
    public String admin() {
        return "This is Admin";
    }

    @GetMapping("/client")
    public String user() {
        return "This is User";
    }

}
