package com.example.shortlinkapplication.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "payments")
public class Payments {
    @Id
    @Column(name = "transactionID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionID;

    @Column(name = "object", length = 20)
    private String object;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "created")
    private Date createdTime;

    @Column(name = "currency", length = 5)
    private String currency;

    @Column(name = "status", length = 10)
    private String status;

}
