package com.example.shortlinkapplication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

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

    @Column(name = "create_date")
    private LocalDate createDate;

    @Column(name = "total_click")
    private Integer totalClick;

    @Column(name = "total_link")
    private Integer totalLink;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User userID;

    public Project(String projectName, String projectSlug, LocalDate createDate, Integer totalClick, Integer totalLink, User userID) {
        this.projectName = projectName;
        this.projectSlug = projectSlug;
        this.createDate = createDate;
        this.totalClick = totalClick;
        this.totalLink = totalLink;
        this.userID = userID;
    }
}
