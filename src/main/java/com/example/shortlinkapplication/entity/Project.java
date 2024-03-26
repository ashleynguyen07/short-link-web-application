package com.example.shortlinkapplication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@Table(name = "project")
@RequiredArgsConstructor
public class Project {

  @Id
  @Column(name = "projectID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer projectID;

  @Column(name = "project_name", length = 20)
  private String projectName;

  @Column(name = "project_slug", length = 20)
  private String projectSlug;

  @Column(name = "domain")
  private String domain;

  @Column(name = "create_date")
  private LocalDate createDate;

  @Column(name = "total_click")
  private Integer totalClick;

  @Column(name = "total_link")
  private Integer totalLink;

  @ManyToOne
  @JoinColumn(name = "userID")
  private User userID;

  public Project(String projectName, String projectSlug, String domain, LocalDate createDate,
      Integer totalClick, Integer totalLink, User userID) {
    this.projectName = projectName;
    this.projectSlug = projectSlug;
    this.createDate = createDate;
    this.totalClick = totalClick;
    this.totalLink = totalLink;
    this.userID = userID;
    this.domain = domain;
  }
}
