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
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Slf4j
public class ProjectController {

  private final ProjectServiceImpl projectService;
  private final UserRepository userRepository;
  private final ProjectRepository projectRepository;

  @GetMapping("/get-project-list")
  public List<Project> getListProject(@CurrentUser UserPrincipal userPrincipal) {
    User userID = getUser(userPrincipal);
    log.info("User: {}", userID);
    return projectService.getListProject(userID);
  }

  @GetMapping("/get-project-by-id")
  public ResponseEntity<?> getProjectByProjectId(@CurrentUser UserPrincipal userPrincipal,
      @Parameter Integer projectID) {
    User userID = getUser(userPrincipal);
    Optional<Project> optionalProject = projectRepository.findById(projectID);
    if (optionalProject.isEmpty()) {
      return new ResponseEntity<>("Project not found", HttpStatus.BAD_REQUEST);
    }
    Project project = optionalProject.get();
    try {
      Project result = projectService.getProjectByProjectId(userID, project);
      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
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

  @DeleteMapping("/delete-project")
  public List<Project> deleteProject(@RequestBody DeleteProjectRequest request,
      @CurrentUser UserPrincipal userPrincipal) {
    User userID = getUser(userPrincipal);
    return projectService.deleteProject(request, userID);
  }

  public User getUser(UserPrincipal userPrincipal) {
    Integer userID = userPrincipal.getId();
    Optional<User> userOptional = userRepository.findById(userID);

    if (userOptional.isPresent()) {
      log.info("User found: {}", userOptional.get());
      return userOptional.get();
    }
    throw new IllegalArgumentException("User not found with id: " + userID);
  }

}