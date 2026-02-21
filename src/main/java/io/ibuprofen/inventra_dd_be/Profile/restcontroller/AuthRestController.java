package io.ibuprofen.inventra_dd_be.Profile.restcontroller;

import io.ibuprofen.inventra_dd_be.Profile.restdto.request.LoginRequest;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.BaseResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.JwtResponse;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.MessageResponse;
import io.ibuprofen.inventra_dd_be.Profile.security.jwt.JwtUtils;
import io.ibuprofen.inventra_dd_be.Profile.security.services.UserDetailsImpl;
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

            JwtResponse jwtResponse = JwtResponse.builder()
                    .token(jwt)
                    .id(userDetails.getId())
                    .name(userDetails.getName())
                    .email(userDetails.getEmail())
                    .role(role)
                    .unit(userDetails.getUnit())
                    .type("Bearer")
                    .build();

            return ResponseEntity.ok(BaseResponseDTO.ok(jwtResponse, "Login successful"));
        } catch (AuthenticationException e) {
            System.err.println("Authentication failed for email: " + loginRequest.getEmail() + " | Error: " + e.getMessage());
            return ResponseEntity.status(401).body(BaseResponseDTO.error(401, "Error: Invalid email or password"));
        } catch (Exception e) {
            System.err.println("Unexpected error during login for email: " + loginRequest.getEmail() + " | Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(BaseResponseDTO.error(500, "Error: An unexpected error occurred"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(BaseResponseDTO.ok(null, "Logout successful"));
    }
}
