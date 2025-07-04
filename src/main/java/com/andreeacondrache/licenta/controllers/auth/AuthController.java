package com.andreeacondrache.licenta.controllers.auth;
import org.springframework.security.access.prepost.PreAuthorize;

import com.andreeacondrache.licenta.dtos.AuthenticationRequest;
import com.andreeacondrache.licenta.dtos.AuthenticationResponse;
import com.andreeacondrache.licenta.dtos.SignupRequest;
import com.andreeacondrache.licenta.dtos.UserDTO;
import com.andreeacondrache.licenta.entities.User;
import com.andreeacondrache.licenta.repositories.UserRepository;
import com.andreeacondrache.licenta.services.auth.AuthService;
import com.andreeacondrache.licenta.services.jwt.UserService;
import com.andreeacondrache.licenta.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import java.util.Optional;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        if (authService.hasUserWithEmail(signupRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("User already exist with the provided email!");
        }
        UserDTO userDTO = authService.signup(signupRequest);
        if (userDTO == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getEmail(),
                    authenticationRequest.getPassword()
            ));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Username or password is incorrect!");
        }
        UserDetails userDetails = userService.userDetailsService().
                loadUserByUsername(authenticationRequest.getEmail());
        Optional<User> optionalUser = userRepository.
                findFirstByEmail(authenticationRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("jwt", jwt);
            responseMap.put("userId", user.getId());
            responseMap.put("userRole", user.getUserRole().name());
            responseMap.put("userName", user.getName()); // ✅ numele userului

            return ResponseEntity.ok(responseMap);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
    }
   // @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'ADMIN')")
    @GetMapping("/test")
    public ResponseEntity<String> testToken() {
        return ResponseEntity.ok("Token primit corect!");
    }

}
