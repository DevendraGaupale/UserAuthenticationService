package com.userauthenticationservice.controllers;

import com.userauthenticationservice.dtos.*;
import com.userauthenticationservice.models.User;
import com.userauthenticationservice.services.AuthService;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto){
        try {
            User user = authService.signUp(signUpRequestDto.getUsername(), signUpRequestDto.getPassword());
            return new ResponseEntity<>(getUserDto(user), HttpStatus.OK);
        } catch (Exception e) {
            throw new IllegalCallerException("User already exists");
        }

    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto){
        try {
            Pair<User, MultiValueMap<String, String>> bodyWithHeaders = authService.login(loginRequestDto.getUsername(), loginRequestDto.getPassword());
            return new ResponseEntity<>(getUserDto(bodyWithHeaders.a),bodyWithHeaders.b, HttpStatus.OK);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestBody ValidateTokenRequestDto validateTokenRequestDto) {
        return new ResponseEntity<>(authService.validateToken(validateTokenRequestDto.getToken()), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<UserDto> logout(@RequestBody LogoutRquestDto logoutRquestDto){
        return null;
    }

    private UserDto getUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setRoleSet(user.getRoleSet());
        return userDto;
    }
}
