package com.example.shortlinkapplication.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.shortlinkapplication.dto.project.CreateProjectRequest;
import com.example.shortlinkapplication.dto.project.DeleteProjectRequest;
import com.example.shortlinkapplication.dto.project.UpdateProjectRequest;
import com.example.shortlinkapplication.entity.Project;
import com.example.shortlinkapplication.entity.User;
import com.example.shortlinkapplication.repository.ProjectRepository;
import com.example.shortlinkapplication.repository.UserRepository;
import com.example.shortlinkapplication.security.UserPrincipal;
import com.example.shortlinkapplication.service.project.ProjectServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ProjectController.class)
@Slf4j
class ProjectControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private ProjectServiceImpl projectService;
  @MockBean
  private ProjectRepository projectRepository;
  @MockBean
  private UserRepository userRepository;
  @Autowired
  private ProjectController projectController;
  private UserPrincipal userPrincipal;
  private Project project;
  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setUserID(1);
    user.setEmail("nguyentramy.se@gmail.com");

    // Mock authentication
    userPrincipal = new UserPrincipal(user.getUserID(), user.getEmail());
    Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null,
        null);
    SecurityContext securityContext = SecurityContextHolder.getContext();
    securityContext.setAuthentication(authentication);

    project = new Project();
    project.setProjectID(1);
    project.setProjectName("short link");
    project.setProjectSlug("short-link");
    project.setUserID(user);

    when(userRepository.findById(user.getUserID())).thenReturn(Optional.of(user));
  }

  /**
   * JUnit test GET request - getListProject method then return project list
   */
  @Test
  @DisplayName("getListProject()")
  void givenUserPrincipal_whenGetListProject_thenReturnProjectList() throws Exception {
    when(projectService.getListProject(user)).thenReturn(Collections.singletonList(project));
    log.info("Project list: {}", projectService.getListProject(user));

    mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/get-project-list")
            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * JUnit test GET request - getListProject method which non-exist project then return exception
   */
  @Test
  @DisplayName("getListProject() with non-exist project")
  void givenNonExistProject_whenGetListProject_thenReturnException() throws Exception {
    when(projectRepository.findById(project.getProjectID())).thenReturn(Optional.empty());

    mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/get-project-by-id")
            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .param("projectID", String.valueOf(project.getProjectID())))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.content().string("Project not found"));
  }

  /**
   * JUnit test GET request - getListProject method with catch exception
   */
  @Test
  @DisplayName("getListProject() with catch exception")
  void givenProject_whenGetListProject_thenCatchException() throws Exception {
    when(projectRepository.findById(project.getProjectID())).thenReturn(Optional.of(project));
    when(projectService.getProjectByProjectId(user, project)).thenThrow(
        new RuntimeException("Unexpected error"));

    mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/get-project-by-id")
            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .param("projectID", String.valueOf(project.getProjectID())))
        .andExpect(MockMvcResultMatchers.status().isInternalServerError())
        .andExpect(MockMvcResultMatchers.content().string("Unexpected error"));
  }

  /**
   * JUnit test GET request - getProjectByProjectId() which project is present
   */
  @Test
  @DisplayName("getProjectByProjectId()")
  void givenProjectID_whenGetProjectByProjectId_thenReturnProjectObject() throws Exception {
    when(projectRepository.findById(project.getProjectID())).thenReturn(Optional.of(project));
    when(projectService.getProjectByProjectId(user, project)).thenReturn(project);
    mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/get-project-by-id")
            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
            .param("projectID", "1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.projectID").value("1"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.projectName").value("short link"));

  }

  /**
   * JUnit test POST request - createProject method
   */
  @Test
  @DisplayName("createProject()")
  void givenCreateProjectRequest_whenCreateProject_thenReturnProjectObject() throws Exception {
    CreateProjectRequest request = new CreateProjectRequest();
    request.setName("short link");
    request.setSlug("short-link");

    when(projectService.createProject(request, user)).thenReturn(project);

    mockMvc.perform(MockMvcRequestBuilders.post("/dashboard/create-project")
            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.projectName").value("short link"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.projectSlug").value("short-link"));
  }

  /**
   * JUnit test PUT request - updateProject method
   */
  @Test
  @DisplayName("updateProject()")
  void givenUpdateProjectRequest_whenUpdateProject_thenReturnProjectObject()
      throws Exception {
    UpdateProjectRequest request = new UpdateProjectRequest();
    request.setProjectID(1);
    request.setName("short application");
    request.setSlug("short-application");

    project.setProjectID(request.getProjectID());
    project.setProjectName(request.getName());
    project.setProjectSlug(request.getSlug());

    when(projectService.updateProject(request, user)).thenReturn(project);

    mockMvc.perform(MockMvcRequestBuilders.put("/dashboard/update-project-info")
            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.projectID").value("1"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.projectName").value("short application"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.projectSlug").value("short-application"));
  }

  /**
   * JUnit test DELETE request - deleteProject method
   */
  @Test
  @DisplayName("deleteProject()")
  void givenDeleteProjectRequest_whenDeleteProject_thenReturnProjectList()
      throws Exception {
    Project project1 = new Project();
    project1.setProjectID(2);
    project1.setProjectName("fpt uni");
    project1.setProjectSlug("fpt-uni");
    project1.setUserID(user);

    DeleteProjectRequest request = new DeleteProjectRequest();
    request.setProjectID(2);
    request.setSlug("fpt-uni");
    request.setVerify("confirm delete project");

    when(projectService.deleteProject(request, user)).thenReturn(
        Collections.singletonList(project));

    mockMvc.perform(MockMvcRequestBuilders.delete("/dashboard/delete-project")
            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value(Matchers.hasSize(1)));
  }

  /**
   * JUnit test getUser method with non-exist user
   */
  @Test
  @DisplayName("getUser() with non-exist user")
  void givenNonExistUser_whenGetUser_thenReturnException() {
    when(userRepository.findById(user.getUserID())).thenReturn(Optional.empty());
    assertThrows(IllegalArgumentException.class, () -> {
      projectController.getUser(userPrincipal);
    });
  }
}