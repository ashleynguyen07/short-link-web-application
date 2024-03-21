package com.example.shortlinkapplication.service.project;

import com.example.shortlinkapplication.dto.project.CreateProjectRequest;
import com.example.shortlinkapplication.dto.project.DeleteProjectRequest;
import com.example.shortlinkapplication.dto.project.UpdateProjectRequest;
import com.example.shortlinkapplication.entity.Project;
import com.example.shortlinkapplication.entity.User;
import com.example.shortlinkapplication.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProjectServiceImpl implements ProjectService{

    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);
    private final ProjectRepository projectRepository;

    @Override
    public List<Project> getListProject(User userID) {
        List<Project> projectList = projectRepository.findProjectByUserID(userID);
        if (projectList.isEmpty()) {
            System.out.println("Project list null");
            return new ArrayList<>();
        } else {
            return projectList;
        }
    }

    @Override
    public Project createProject(CreateProjectRequest request, User userID) {
        String name = request.getName();
        String slug = request.getSlug();
        LocalDate createDate = LocalDate.now();
        Integer totalLink = null;
        Integer totalClick = null;

        Project project = new Project(name, slug, createDate, totalClick, totalLink, userID);
        projectRepository.save(project);

        System.out.println("Creating project..");
        logger.info(String.valueOf(project));

        return project;
    }

    @Override
    public Project updateProject(UpdateProjectRequest request, User userID) {
        Optional<Project> optionalProject = projectRepository.findById(request.getProjectID());
        logger.info(String.valueOf(optionalProject));

        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();

            project.setProjectName(request.getName());
            project.setProjectSlug(request.getSlug());
            projectRepository.save(project);

            return project;
        }
        throw new RuntimeException("Project not found with id: " + request.getProjectID());
    }

    @Override
    public List<Project> deleteProject(DeleteProjectRequest request, User userID) {
        Integer projectID = request.getProjectID();
        String slug = request.getSlug();
        String verify = request.getVerify();

        // get slug => projectID
        Optional<Project> optionalProject = projectRepository.findById(request.getProjectID());
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            if (project.getProjectSlug().equals(slug) && verify.equals("confirm delete project")) {
                projectRepository.deleteById(projectID);
                logger.info(String.valueOf(getListProject(userID)));
                return getListProject(userID);
            } else {
                throw new RuntimeException("The request does not match the slug or verify string of project!");
            }
        } else {
            throw new RuntimeException("The request does not match the slug of project!");
        }
    }

}
