package com.example.shortlinkapplication.dto;

import com.example.shortlinkapplication.entity.ConfirmationToken;
import lombok.Data;

@Data
public class LoginResponse {
    private String token;
}
