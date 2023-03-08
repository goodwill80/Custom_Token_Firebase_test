package com.jonathan.firebase_custom_token.Security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.jonathan.firebase_custom_token.Entity.Users;
import com.jonathan.firebase_custom_token.Repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Optional;

@Component
public class FirebaseFilter extends OncePerRequestFilter {

    @Autowired
    UserRepository userRepo;


    @Override
    protected void doFilterInternal (
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        Logger logger = LoggerFactory.getLogger(getClass());

        // a. Check for Auth Header and return if invalid
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            logger.info("No bearer header in JWT");
            return;
        }
        FirebaseToken decodedToken = null;
        String token = authHeader.replace("Bearer ", "");

        // b. Extract header and send to firebase for verifications
        try {
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            logger.info("Verified JWT with user details of " +
                    decodedToken.getIssuer() + " " + decodedToken.getEmail());
        } catch(Exception ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error!");
        }

        // c. if token is invalid, throw exception
        if (decodedToken==null){
            logger.info("NULL Token");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid token!");
        }

        // d. Checked if user's record is already in AuthContext
        String username = decodedToken.getEmail();
        if(SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<Users> userDetails = userRepo.findByUsername(username);
            if(userDetails.isPresent()) {
                AddToContextHolder(userDetails.get());
            }
        }
        // If all okay, continue with filter chain
        filterChain.doFilter(request,response);
    }

    // Helper to Add user into security context holder
    protected void AddToContextHolder(UserDetails user) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());
        // pass into security context holder
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
