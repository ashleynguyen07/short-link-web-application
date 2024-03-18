package com.example.shortlinkapplication.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "devices")
public class Devices {
    @Id
    @Column(name = "deviceID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deviceID;

    @Column(name = "device", length = 50)
    private String device;

    @Column(name = "browser", length = 50)
    private String browser;
}
