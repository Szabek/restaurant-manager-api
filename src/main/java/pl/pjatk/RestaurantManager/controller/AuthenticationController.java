package pl.pjatk.RestaurantManager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.RestaurantManager.auth.AuthenticationRequest;
import pl.pjatk.RestaurantManager.auth.AuthenticationResponse;
import pl.pjatk.RestaurantManager.auth.RegisterRequest;
import pl.pjatk.RestaurantManager.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin()
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(   //TODO: the possibility of registration is to be removed afterwards - replace with adding users by ADMIN
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }
    @CrossOrigin
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
