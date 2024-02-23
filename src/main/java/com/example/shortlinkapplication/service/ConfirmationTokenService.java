package com.example.shortlinkapplication.service;

import com.example.shortlinkapplication.entity.ConfirmationToken;
import com.example.shortlinkapplication.repository.ConfirmationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenService {
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    // save token
    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    // get token
    public ConfirmationToken getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    // set confirmed at
    public int setConfirmedAt(String token) {
        return confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }
}