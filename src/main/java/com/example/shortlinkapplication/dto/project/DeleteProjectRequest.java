package com.example.shortlinkapplication.dto.project;

import lombok.Data;

@Data
public class DeleteProjectRequest {
    Integer projectID;
    String slug;
    String verify;
}
