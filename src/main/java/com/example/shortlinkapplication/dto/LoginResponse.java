package com.example.shortlinkapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
}
