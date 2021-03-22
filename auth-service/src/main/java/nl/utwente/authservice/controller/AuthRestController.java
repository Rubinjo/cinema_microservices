package nl.utwente.authservice.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import nl.utwente.authservice.models.AuthenticationRequest;
import nl.utwente.authservice.models.AuthenticationResponse;
import nl.utwente.authservice.services.MyUserDetailsService;
import nl.utwente.authservice.util.JwtUtil;

@RestController
public class AuthRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @RequestMapping("/authenticate/check")
    public Map<String, String> checkAccount() {
        HashMap<String, String> response = new HashMap<>();
        response.put("Access", "OK");
        return response;
    }

    @RequestMapping("/authenticate/account/new")
    public Map<String, String> newAccount(@RequestBody AuthenticationRequest authenticationRequest) {
        HashMap<String, String> response = new HashMap<>();
        try {
            UserDetails user = new User(authenticationRequest.getUsername(), authenticationRequest.getPassword(), new ArrayList<>());
            // userDetailsManager.createUser(user);
            response.put("Access", "OK");
        } catch(Exception e) {
            response.put("Access", "FAILED");
        }
        
        return response;
    }

    @RequestMapping(value="/authenticate/account/login", method=RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

}