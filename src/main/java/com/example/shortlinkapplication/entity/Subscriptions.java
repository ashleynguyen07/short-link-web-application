package com.example.shortlinkapplication.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "subscriptions")
public class Subscriptions {
    @Id
    @Column(name = "subscriptionID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer subscriptionID;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User userID;

    @Column(name = "address")
    private String address;

    @Column(name = "startDate")
    private Date startDate;

    @Column(name = "endDate")
    private Date endDate;

    @Column(name = "remainingTime")
    private Integer remainingTime;

}
