package com.example.shortlinkapplication.controller;

import com.example.shortlinkapplication.dto.project.CreateProjectRequest;
import com.example.shortlinkapplication.dto.project.DeleteProjectRequest;
import com.example.shortlinkapplication.dto.project.UpdateProjectRequest;
import com.example.shortlinkapplication.entity.Project;
import com.example.shortlinkapplication.entity.User;
import com.example.shortlinkapplication.repository.ProjectRepository;
import com.example.shortlinkapplication.repository.UserRepository;
import com.example.shortlinkapplication.security.CurrentUser;
import com.example.shortlinkapplication.security.UserPrincipal;
import com.example.shortlinkapplication.service.project.ProjectServiceImpl;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@AllArgsConstructor
public class ProjectController {

  private final ProjectServiceImpl projectService;
  private final UserRepository userRepository;
  private final ProjectRepository projectRepository;

  @GetMapping("/get-project-list")
  public List<Project> getListProject(@CurrentUser UserPrincipal userPrincipal) {
    User userID = getUser(userPrincipal);
    return projectService.getListProject(userID);
  }

  @PostMapping("/create-project")
  public Project createProject(@RequestBody CreateProjectRequest request,
      @CurrentUser UserPrincipal userPrincipal) {
    User userID = getUser(userPrincipal);
    return projectService.createProject(request, userID);
  }

  @PutMapping("/update-project-info")
  public Project updateProject(@RequestBody UpdateProjectRequest request,
      @CurrentUser UserPrincipal userPrincipal) {
    User userID = getUser(userPrincipal);
    return projectService.updateProject(request, userID);
  }

  @DeleteMapping("delete-project")
  public List<Project> deleteProject(@RequestBody DeleteProjectRequest request,
      @CurrentUser UserPrincipal userPrincipal) {
    User userID = getUser(userPrincipal);
    return projectService.deleteProject(request, userID);
  }

  private User getUser(UserPrincipal userPrincipal) {
    Integer userID = userPrincipal.getId();
    Optional<User> userOptional = userRepository.findById(userID);

    if (userOptional.isPresent()) {
      return userOptional.get();
    }
    throw new IllegalStateException("User not found with id: " + userID);
  }

}