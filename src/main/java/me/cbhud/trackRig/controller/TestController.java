package me.cbhud.trackRig.controller;

import me.cbhud.trackRig.security.SecurityUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/me")
    public String me(@AuthenticationPrincipal SecurityUser user) {
        return "Hello " + user.getUsername() + ", your role is " + user.getRole().name();
    }

    @GetMapping("/e")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String employeeOnly(@AuthenticationPrincipal SecurityUser user) {
        return "EMPLOYEE endpoint: " + user.getUsername();
    }

    @GetMapping("/m")
    @PreAuthorize("hasRole('MANAGER')")
    public String managerOnly(@AuthenticationPrincipal SecurityUser user) {
        return "MANAGER endpoint: " + user.getUsername();
    }

    @GetMapping("/o")
    @PreAuthorize("hasRole('OWNER')")
    public String ownerOnly(@AuthenticationPrincipal SecurityUser user) {
        return "OWNER endpoint: " + user.getUsername();
    }

    @GetMapping("/om")
    @PreAuthorize("hasAnyRole('MANAGER', 'OWNER')")
    public String managerOrOwner(@AuthenticationPrincipal SecurityUser user) {
        return "MANAGER or OWNER endpoint: " + user.getUsername();
    }
}