package com.example.shortlinkapplication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "confirm_token")
public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "token")
    private String token;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User userID;

    public ConfirmationToken(){}
    public ConfirmationToken(String token, LocalDateTime confirmedAt) {
        this.token = token;
        this.confirmedAt = confirmedAt;
    }
    public ConfirmationToken(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public ConfirmationToken(String token, LocalDateTime createdAt, LocalDateTime expiredAt, User userID) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.userID = userID;
    }

}
