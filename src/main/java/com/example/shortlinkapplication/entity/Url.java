package com.example.shortlinkapplication.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "url")
public class Url {
    @Id
    @Column(name = "shortLink", length = 16)
    private String shortLink;

    @Column(name = "originalURL")
    private String originalURL;

    @Column(name = "creationDate")
    private Date createtionDate;

    @Column(name = "expirationDate")
    private Date expirationDate;

    @ManyToOne(fetch = FetchType.LAZY) // fetch type LAZY is default
    @JoinColumn(name = "userID")
    private User userID;
}
