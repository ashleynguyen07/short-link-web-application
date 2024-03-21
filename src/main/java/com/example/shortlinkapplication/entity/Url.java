package com.example.shortlinkapplication.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@Table(name = "url")
public class Url {
    @Id
    @Column(name = "short_url", length = 16)
    private String shortUrl;

    @Column(name = "long_url")
    private String longUrl;

    @Column(name = "create_date")
    private LocalDate creationDate;

    @Column(name = "expire_date")
    private LocalDate expirationDate;

    @ManyToOne(fetch = FetchType.LAZY) // fetch type LAZY is default
    @JoinColumn(name = "userID")
    private User userID;


}
