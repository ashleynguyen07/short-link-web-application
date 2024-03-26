package com.example.shortlinkapplication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Data
@Table(name = "url")
public class Url {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "long_url")
  private String longUrl;

  @Column(name = "short_url")
  private String shortUrl;

  @Column(name = "create_date")
  private LocalDate creationDate;

  @Column(name = "expire_date")
  private LocalDate expirationDate;

  @ManyToOne(fetch = FetchType.EAGER) // fetch type LAZY is default
  @JoinColumn(name = "projectID")
  private Project projectID;

}
