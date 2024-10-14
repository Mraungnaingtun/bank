package com.logant.BankAccountManagementSystem.Auth;

import com.logant.BankAccountManagementSystem.Auth.dto.AuthResponseDto;
import com.logant.BankAccountManagementSystem.Auth.dto.LoginRequestDto;
import com.logant.BankAccountManagementSystem.Auth.dto.UserRegistrationDto;
import com.logant.BankAccountManagementSystem.General.MainResponse;
import com.logant.BankAccountManagementSystem.General.ResponseCode;
import com.logant.BankAccountManagementSystem.Log.LoggingService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4202")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthService authService;


    @Autowired
    private LoggingService loggingService;

    // -------------------login---------------------------------------------
    @PostMapping("/sign-in")
    public ResponseEntity<MainResponse> authenticateUser(@RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse response) {
        AuthResponseDto ret = new AuthResponseDto();
        loggingService.log("INFO", loginRequestDto.getUsername());
        
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginRequestDto.getUsername(), loginRequestDto.getPassword());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            ret = authService.getJwtTokensAfterAuthentication(authentication, response);

        } catch (Exception e) {
            if (e.getMessage().equals("Bad credentials")) {
                return MainResponse.buildErrorResponse(ResponseCode.WRONG_CREDENTIALS, e.getMessage(),
                        HttpStatus.UNAUTHORIZED);
            }
            return MainResponse.buildErrorResponse(ResponseCode.SERVER_ERROR, e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return MainResponse.buildSuccessResponse(ResponseCode.SUCCESS, ret, HttpStatus.OK);
    }

    // -------------------refresh token---------------------------------------------
    @PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
    @PostMapping("/refresh-token")
    public ResponseEntity<MainResponse> getAccessToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        AuthResponseDto ret = new AuthResponseDto();
        try {

            ret = authService.getAccessTokenUsingRefreshToken(authorizationHeader);

        } catch (Exception err) {
            return MainResponse.buildErrorResponse(ResponseCode.SERVER_ERROR, err.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return MainResponse.buildSuccessResponse(ResponseCode.SUCCESS, ret, HttpStatus.OK);
    }

    // -------------------register---------------------------------------------
    @PostMapping("/sign-up")
    public ResponseEntity<MainResponse> registerUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto,
            BindingResult bindingResult, HttpServletResponse httpServletResponse) {

        AuthResponseDto ret = new AuthResponseDto();
        try {
            log.info("[AuthController:registerUser]Signup Process Started for user:{}", userRegistrationDto.userName());

            if (bindingResult.hasErrors()) {
                List<String> errorMessage = bindingResult.getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toList();
                log.error("[AuthController:registerUser]Errors in user:{}", errorMessage);

                return MainResponse.buildErrorResponse(ResponseCode.SERVER_ERROR, errorMessage.toString(),
                        HttpStatus.BAD_REQUEST);
            }

            ret = authService.registerUser(userRegistrationDto, httpServletResponse);

        } catch (Exception err) {
            return MainResponse.buildErrorResponse(ResponseCode.SERVER_ERROR, err.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return MainResponse.buildSuccessResponse(ResponseCode.SUCCESS, ret, HttpStatus.OK);
    }
}
