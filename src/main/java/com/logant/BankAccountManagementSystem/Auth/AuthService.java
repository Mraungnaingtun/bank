package com.logant.BankAccountManagementSystem.Auth;

import com.logant.BankAccountManagementSystem.Security.Entity.RefreshTokenEntity;
import com.logant.BankAccountManagementSystem.Security.Repository.RefreshTokenRepo;
import com.logant.BankAccountManagementSystem.Security.Repository.UserInfoRepo;
import com.logant.BankAccountManagementSystem.Auth.dto.AuthResponseDto;
import com.logant.BankAccountManagementSystem.Security.Enum.TokenType;
import com.logant.BankAccountManagementSystem.Auth.dto.UserRegistrationDto;
import com.logant.BankAccountManagementSystem.Security.mapper.UserInfoMapper;
import com.logant.BankAccountManagementSystem.Security.config.jwtAuth.JwtTokenGenerator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserInfoRepo userInfoRepo;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserInfoMapper userInfoMapper;

    @Value("${jwt.access-token.expiration}")
    private int accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private int refreshTokenExpiration;

    public AuthResponseDto getJwtTokensAfterAuthentication(Authentication authentication,
            HttpServletResponse response) {
        try {
            var userInfoEntity = userInfoRepo.findByEmailId(authentication.getName())
                    .orElseThrow(() -> {
                        log.error("[AuthService:userSignInAuth] User :{} not found", authentication.getName());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "USER NOT FOUND ");
                    });

            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
            String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);
            // Let's save the refreshToken as well
            saveUserRefreshToken(userInfoEntity, refreshToken);
            // Creating the cookie
            creatRefreshTokenCookie(response, refreshToken);
            log.info("[AuthService:userSignInAuth] Access token for user:{}, has been generated",
                    userInfoEntity.getUserName());
            return AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .accessTokenExpiry(accessTokenExpiration * 60)
                    .userName(userInfoEntity.getUserName())
                    .tokenType(TokenType.Bearer)
                    .build();
        } catch (Exception e) {
            log.error("[AuthService:userSignInAuth]Exception while authenticating the user due to :" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Please Try Again");
        }
    }

    private void saveUserRefreshToken(User userInfoEntity, String refreshToken) {
        var refreshTokenEntity = RefreshTokenEntity.builder()
                .user(userInfoEntity)
                .refreshToken(refreshToken)
                .revoked(false)
                .build();
        refreshTokenRepo.save(refreshTokenEntity);
    }

    private Cookie creatRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(refreshTokenExpiration * 60); // in seconds
        response.addCookie(refreshTokenCookie);
        return refreshTokenCookie;
    }

    public AuthResponseDto getAccessTokenUsingRefreshToken(String authorizationHeader) {

        if (!authorizationHeader.startsWith(TokenType.Bearer.name())) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Please verify your token type");
        }

        final String refreshToken = authorizationHeader.substring(7);

        // Find refreshToken from database and ensure it is not revoked
        Optional<RefreshTokenEntity> refreshTokenEntity = refreshTokenRepo.findByRefreshToken(refreshToken)
                .filter(tokens -> !tokens.isRevoked());

        if (!refreshTokenEntity.isPresent()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Refresh token revoked");
        }

        RefreshTokenEntity res = refreshTokenEntity.get();
        if (res.isRevoked()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Refresh token revoked");
        }

        User userInfoEntity = res.getUser();

        // Create the Authentication object
        Authentication authentication = createAuthenticationObject(userInfoEntity);

        // Update the existing refresh token entity instead of creating a new one
        res.setRevoked(true); // Mark the token as revoked
        refreshTokenRepo.save(res); // Save the updated entity

        // Generate a new access token
        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiry(accessTokenExpiration * 60)
                .userName(userInfoEntity.getUserName())
                .tokenType(TokenType.Bearer)
                .build();
    }

    private static Authentication createAuthenticationObject(User userInfoEntity) {
        // Extract user details from UserDetailsEntity
        String username = userInfoEntity.getEmailId();
        String password = userInfoEntity.getPassword();
        String roles = userInfoEntity.getRoles();

        // Extract authorities from roles (comma-separated)
        String[] roleArray = roles.split(",");
        GrantedAuthority[] authorities = Arrays.stream(roleArray)
                .map(role -> (GrantedAuthority) role::trim)
                .toArray(GrantedAuthority[]::new);

        return new UsernamePasswordAuthenticationToken(username, password, Arrays.asList(authorities));
    }

    public AuthResponseDto registerUser(UserRegistrationDto userRegistrationDto,
            HttpServletResponse httpServletResponse) {
        try {
            log.info("[AuthService:registerUser]User Registration Started with :::{}", userRegistrationDto);

            Optional<User> user = userInfoRepo.findByEmailId(userRegistrationDto.userEmail());
            if (user.isPresent()) {
                throw new Exception("User Already Exist");
            }

            User userDetailsEntity = userInfoMapper.convertToEntity(userRegistrationDto);
            Authentication authentication = createAuthenticationObject(userDetailsEntity);

            // Generate a JWT token
            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
            String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);

            User savedUserDetails = userInfoRepo.save(userDetailsEntity);
            saveUserRefreshToken(userDetailsEntity, refreshToken);

            creatRefreshTokenCookie(httpServletResponse, refreshToken);

            log.info("[AuthService:registerUser] User:{} Successfully registered", savedUserDetails.getUserName());
            return AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .accessTokenExpiry(accessTokenExpiration * 60)
                    .userName(savedUserDetails.getUserName())
                    .tokenType(TokenType.Bearer)
                    .build();

        } catch (Exception e) {
            log.error("[AuthService:registerUser]Exception while registering the user due to :" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
