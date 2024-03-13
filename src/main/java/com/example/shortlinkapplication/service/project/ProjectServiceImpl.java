package com.example.shortlinkapplication.service.project;

import com.example.shortlinkapplication.dto.project.CreateProjectRequest;
import com.example.shortlinkapplication.dto.project.DeleteProjectRequest;
import com.example.shortlinkapplication.dto.project.UpdateProjectRequest;
import com.example.shortlinkapplication.entity.Project;
import com.example.shortlinkapplication.entity.User;
import com.example.shortlinkapplication.repository.ProjectRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProjectServiceImpl implements ProjectService {

  private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);
  private final ProjectRepository projectRepository;

  /**
   * get list project from userID
   *
   * @return list project
   */
  @Override
  public List<Project> getListProject(User userID) {
    List<Project> projectList = projectRepository.findProjectByUserID(userID);
    if (projectList.isEmpty()) {
      logger.info("Project list null");
      return new ArrayList<>();
    } else {
      return projectList;
    }
  }

  /**
   * create new project with userID
   *
   * @param request, userID
   * @return Project
   */
  @Override
  public Project createProject(CreateProjectRequest request, User userID) {
    String name = request.getName();
    String slug = request.getSlug();
    LocalDate createDate = LocalDate.now();
    Integer totalLink = null;
    Integer totalClick = null;
    String domain = "ma.rs";

    Project project = new Project(name, slug, domain, createDate, totalClick, totalLink, userID);
    projectRepository.save(project);

    logger.info("Creating project..");
    logger.info("Project: {}", project);

    return project;
  }

  /**
   * update exist project information with userID
   *
   * @param request, userID
   * @return list project
   */
  @Override
  public Project updateProject(UpdateProjectRequest request, User userID) {
    Optional<Project> optionalProject = projectRepository.findById(request.getProjectID());
    logger.info("Find project by userID: {}", projectRepository.findProjectByUserID(userID));

    if (optionalProject.isPresent()) {
      Project project = optionalProject.get();
      User findUserID = projectRepository.findUserIDByProjectID(project.getProjectID());
      if (!userID.equals(findUserID)) {
        logger.info("UserID: {}", userID);
        logger.info("Find user id: {}", findUserID);
        logger.error("Handler policy user update");
        throw new IllegalArgumentException("Handler policy user update");
      } else {
        project.setProjectName(request.getName());
        project.setProjectSlug(request.getSlug());
        projectRepository.save(project);

        return project;
      }

    }
    logger.error("No exist project of the user ID: {}", userID);
    throw new IllegalArgumentException("Project not found with id: " + request.getProjectID());
  }

  /**
   * delete project from userID
   *
   * @param request, useID
   * @return list project
   */
  @Override
  public List<Project> deleteProject(DeleteProjectRequest request, User userID) {
    Integer projectID = request.getProjectID();
    String slug = request.getSlug();
    String verify = request.getVerify();

    // get slug => projectID
    Optional<Project> optionalProject = projectRepository.findById(request.getProjectID());
    if (optionalProject.isEmpty()) {
      logger.error("Not found project with projectID: {}", projectID);
      throw new IllegalStateException("Not found project with projectID!");
    } else {
      Project project = optionalProject.get();
      User findUserID = projectRepository.findUserIDByProjectID(project.getProjectID());

      if (!userID.equals(findUserID)) {
        logger.error("The request does not match userID!");
        throw new IllegalStateException("The request does not match userID!");

      } else if (project.getProjectSlug().equals(slug) && verify.equals("confirm delete project")) {
        projectRepository.deleteById(projectID);
        logger.info("Get project list: {}", getListProject(userID));
        return getListProject(userID);
      } else {
        throw new IllegalArgumentException(
            "The request does not match the slug or verify string of project!");
      }
    }
  }

}