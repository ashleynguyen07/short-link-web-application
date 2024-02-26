package com.example.shortlinkapplication.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "project")
public class Project {
    @Id
    @Column(name = "projectID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer projectID;

    @Column(name = "projectName", length = 20)
    private String projectName;

    @Column(name = "projectSlug", length = 20)
    private String projectSlug;

    @Column(name = "creationDate")
    private Date creationDate;

    @ManyToOne
    @JoinColumn(name = "shortLink")
    private Url shortLink;

}
