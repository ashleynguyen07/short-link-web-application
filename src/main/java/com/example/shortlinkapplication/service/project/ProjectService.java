package com.example.shortlinkapplication.service.project;

import com.example.shortlinkapplication.dto.project.CreateProjectRequest;
import com.example.shortlinkapplication.dto.project.DeleteProjectRequest;
import com.example.shortlinkapplication.dto.project.UpdateProjectRequest;
import com.example.shortlinkapplication.entity.Project;
import com.example.shortlinkapplication.entity.User;

import java.util.List;

public interface ProjectService {

  List<Project> getListProject(User userID);

  Project getProjectByProjectId(User userID, Project projectID);

  Project createProject(CreateProjectRequest request, User userID);

  Project updateProject(UpdateProjectRequest request, User userID);

  List<Project> deleteProject(DeleteProjectRequest request, User id);
}
