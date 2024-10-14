package com.logant.BankAccountManagementSystem.General;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class MainResponse {
    private boolean succces;
    private String retcode;
    private String retmsg;
    private Object data;

    public static ResponseEntity<MainResponse> buildSuccessResponse(ResponseCode code, Object data, HttpStatus status) {
        // log.info(code.getMessage(), data);
        return new ResponseEntity<>(
                MainResponse.builder()
                        .retcode(code.getCode())
                        .retmsg(code.getMessage())
                        .data(data)
                        .build(),
                status);
    }

    public static ResponseEntity<MainResponse> buildErrorResponse(ResponseCode code, String message, HttpStatus status) {
        // log.error(message, code);
        return new ResponseEntity<>(
                MainResponse.builder()
                        .succces(false)
                        .retcode(code.getCode())
                        .retmsg(message)
                        .build(),
                status);
    }
}
