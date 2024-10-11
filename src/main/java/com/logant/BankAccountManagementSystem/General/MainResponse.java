package com.logant.BankAccountManagementSystem.General;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainResponse {
    private boolean succces;
    private String retcode;
    private String retmsg;
    private Object data;

    public static ResponseEntity<MainResponse> buildSuccessResponse(ResponseCode code, Object data, HttpStatus status) {
        return new ResponseEntity<>(
                MainResponse.builder()
                        .succces(true)
                        .retcode(code.getCode())
                        .retmsg(code.getMessage())
                        .data(data)
                        .build(),
                status);
    }

    public static ResponseEntity<MainResponse> buildErrorResponse(ResponseCode code, String message, HttpStatus status) {
        return new ResponseEntity<>(
                MainResponse.builder()
                        .succces(false)
                        .retcode(code.getCode())
                        .retmsg(message)
                        .build(),
                status);
    }
}
