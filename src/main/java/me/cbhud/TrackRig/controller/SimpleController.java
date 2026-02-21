package me.cbhud.TrackRig.controller;

import me.cbhud.TrackRig.model.AppUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class SimpleController {

    @GetMapping("/test")
    public String helloWorld(@AuthenticationPrincipal AppUser user){
        String name = user.getFullName();
        String role = user.getRole().toString();
        return "Hello, " + name + "\nYou are: " + role;
    }
}
