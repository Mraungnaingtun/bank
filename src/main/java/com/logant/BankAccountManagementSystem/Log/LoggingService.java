package com.logant.BankAccountManagementSystem.Log;

import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoggingService {

    @Autowired
    private final LogEntryRepository logEntryRepository;

    public void log(String level, String message) {
        Log logEntry = new Log();
        logEntry.setLevel(level);
        logEntry.setMessage(message);
        logEntry.setTimestamp(LocalDateTime.now());

        //------save-----to db
        logEntryRepository.save(logEntry);
    }
}
