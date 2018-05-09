/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dot.gcpbasedot.dto.UserByToken;
import java.io.UnsupportedEncodingException;
/**
 *
 * @author grupot
 */
public class JwtUtil {

    /**
     * Generates a JWT token containing username as subject, and userId and role as additional claims. These properties are taken from the specified
     * User object. Tokens validity is infinite.
     * 
     * @param u the user for which the token will be generated
     * @param secret
     * @return the JWT token
     */
    public String generateToken(UserByToken u, String secret) {
        try {
            String token = JWT.create()
                .withClaim("username", u.getUsername())
                .withClaim("password", u.getPassword())
                .withClaim("creation", u.getCreation())
                .withClaim("expiration", u.getExpiration())
                .sign(Algorithm.HMAC256(secret));
            
            return token;
        } catch (UnsupportedEncodingException | JWTCreationException exception){
            //UTF-8 encoding not supported
        }
        //Invalid Signing configuration / Couldn't convert Claims.
        return null;
    }
    
    /**
     * Tries to parse specified String as a JWT token. If successful, returns User object with username, id and role prefilled (extracted from token).
     * If unsuccessful (token is invalid or not containing all required user properties), simply returns null.
     * 
     * @param token the JWT token to parse
     * @param secret
     * @return the User object extracted from specified token or null if a token is invalid.
     */
    public UserByToken parseToken(String token, String secret) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            DecodedJWT jwt = verifier.verify(token);
            
            UserByToken u = new UserByToken();
            u.setUsername(jwt.getClaim("username").asString());
            u.setPassword(jwt.getClaim("password").asString());
            u.setCreation(jwt.getClaim("creation").asLong());
            u.setExpiration(jwt.getClaim("expiration").asLong());
            
            return u;
        } catch (UnsupportedEncodingException | JWTVerificationException ex){
            //UTF-8 encoding not supported
        }
        //Invalid signature/claims
        return null;
    }
    
}
