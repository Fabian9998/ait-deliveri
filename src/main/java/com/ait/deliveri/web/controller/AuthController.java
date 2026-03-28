package com.ait.deliveri.web.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ait.deliveri.utils.JwtUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("auth/login")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Login para consumo de apis")
public class AuthController {
	
	private final AuthenticationManager authenticationManager;

	@PostMapping
	@Operation(summary = "Login retorna JWT")
	public String login(@RequestParam String username, @RequestParam String password) {
		authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        return JwtUtils.generarToken(username);
	}
}
