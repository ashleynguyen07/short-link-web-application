package com.example.shortlinkapplication.dto.project;

import com.example.shortlinkapplication.entity.Project;
import lombok.Data;

@Data
public class UpdateProjectRequest {
    Integer projectID;
    String name;
    String slug;
}
