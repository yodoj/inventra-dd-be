package io.ibuprofen.inventra_dd_be.restcontroller;

import io.ibuprofen.inventra_dd_be.restdto.JwtResponse;
import io.ibuprofen.inventra_dd_be.restdto.LoginRequest;
import io.ibuprofen.inventra_dd_be.restdto.MessageResponse;
import io.ibuprofen.inventra_dd_be.security.jwt.JwtUtils;
import io.ibuprofen.inventra_dd_be.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("Login attempt for email: " + loginRequest.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(item -> item.getAuthority())
                    .orElse(null);

            System.out.println("Login successful for user: " + userDetails.getEmail());
            
            return ResponseEntity.ok(JwtResponse.builder()
                    .token(jwt)
                    .id(userDetails.getId())
                    .name(userDetails.getName())
                    .email(userDetails.getEmail())
                    .role(role)
                    .unit(userDetails.getUnit())
                    .type("Bearer")
                    .build());
        } catch (AuthenticationException e) {
            System.err.println("Authentication failed for email: " + loginRequest.getEmail() + " | Error: " + e.getMessage());
            return ResponseEntity.status(401).body(new MessageResponse("Error: Invalid email or password"));
        } catch (Exception e) {
            System.err.println("Unexpected error during login for email: " + loginRequest.getEmail() + " | Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new MessageResponse("Error: An unexpected error occurred"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("Logout successful"));
    }
}
