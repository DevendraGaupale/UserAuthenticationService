package com.userauthenticationservice.services;

import com.userauthenticationservice.models.Session;
import com.userauthenticationservice.models.SessionState;
import com.userauthenticationservice.models.User;
import com.userauthenticationservice.repositories.SessionRepository;
import com.userauthenticationservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    final String jwtSecret = "ADGJK6758wErt54zx243DhsSwetHTse0GHJ623YUiPo65fsfs7";

    public User signUp(String username, String password) {

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            return null;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);

        return user;
    }

    public Pair<User, MultiValueMap<String, String>> login(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return null;
        }

        if(!bCryptPasswordEncoder.matches(password, optionalUser.get().getPassword())){
            return null;
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", optionalUser.get().getUsername());
        claims.put("roles", optionalUser.get().getRoleSet());
        long nowInMillis = System.currentTimeMillis();
        claims.put("iat", nowInMillis);
        claims.put("exp", nowInMillis+600000);

//        MacAlgorithm algorithm = Jwts.SIG.HS256;
//        SecretKey secretKey = algorithm.key().build();

//        SignatureAlgorithm sa = SignatureAlgorithm.HS256;
//        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), sa.getJcaName());

//        SignatureAlgorithm sa = SignatureAlgorithm.HS256;
//        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");

//        String token = Jwts.builder().claims(claims)
//                .signWith(secretKeySpec).compact();

        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);

        String token = Jwts.builder().claims(claims)
                .signWith(secretKey).compact();

//        String token = Jwts.builder().claims(claims)
//                .signWith(SignatureAlgorithm.HS256,secretKey).compact();

        //Storing Session for validation purpose
        Session session = new Session();
        session.setUser(optionalUser.get());
        session.setToken(token);
        session.setSessionState(SessionState.ACTIVE);
        sessionRepository.save(session);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.SET_COOKIE, token);

        return new Pair<User, MultiValueMap<String, String>>(optionalUser.get(), headers);
    }

    public Boolean validateToken(String token) {

        Optional<Session> session = sessionRepository.findByToken(token);
        if (session.isEmpty()) {
            System.out.println("Invalid token");
            return false;
        }

//        SignatureAlgorithm sa = SignatureAlgorithm.HS256;
//        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), sa.getJcaName());
//        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HS256");
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();
        Long tokenExpiryTime = claims.get("exp", Long.class);
        Long nowInMillis = System.currentTimeMillis();

        if(nowInMillis > tokenExpiryTime){
            System.out.println("Token is expired");
            return false;
        }



        return true;
    }
}
