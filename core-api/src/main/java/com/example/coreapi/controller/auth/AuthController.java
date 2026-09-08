package com.example.coreapi.controller.auth;

import com.example.common.entity.User;
import com.example.common.mappers.responses.UserView;
import com.example.common.payload.request.UserRequest;
import com.example.common.util.RestApiResponse;
import com.example.coreapi.configuration.security.JwtService;
import com.example.coreapi.service.AuthenticationService;
import com.example.coreapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/register")
    public RestApiResponse<UserView> register(@RequestBody UserRequest request) {
        return new RestApiResponse<>("200", "Success", userService.save(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserLoginRequest loginRequest) {
        User authenticatedUser = authenticationService.authenticate(loginRequest);

        String accessToken = jwtService.generateAccessToken(authenticatedUser.getEmail());
        String refreshToken = jwtService.generateRefreshToken(authenticatedUser.getEmail());

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return ResponseEntity.ok(loginResponse);
    }
}