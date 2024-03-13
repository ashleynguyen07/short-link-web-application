package com.example.shortlinkapplication.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "locations")
public class Locations {
    @Id
    @Column(name = "locationID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer locationID;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "city", length = 50)
    private String city;
}
