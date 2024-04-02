package com.example.shortlinkapplication.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.shortlinkapplication.entity.Project;
import com.example.shortlinkapplication.entity.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProjectRepositoryTest {

  @Autowired
  private ProjectRepository projectRepository;
  @Autowired
  private UserRepository userRepository;
  private User user;
  private Project project;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setUserID(1);
    user.setName("My");
    user.setEmail("nguyentramy.se@gmail.com");
    userRepository.save(user);

    project = new Project();
    project.setProjectID(1);
    project.setProjectName("shortLinkApplication");
    project.setUserID(user);
    projectRepository.save(project);
  }

  /**
   * JUnit test findProjectByUserID method
   */
  @Test
  @DisplayName("findProjectByUserID()")
  void givenUserID_whenFindProjectByUserID_thenReturnProjectList() {
    Optional<User> userOptional = userRepository.findById(user.getUserID());
    if (userOptional.isEmpty()) {
      throw new IllegalStateException("User not found!");
    }
    List<Project> foundProject = projectRepository.findProjectByUserID((userOptional.get()));

    assertThat(foundProject).hasSize(1);
  }

  /**
   * JUnit test findUserIDByProjectID method
   */
  @Test
  @DisplayName("findUserIDByProjectID()")
  void givenProjectID_whenFindUserIDByProjectID_thenReturnUserID() {
    User userID = projectRepository.findUserIDByProjectID(project.getProjectID());
    assertThat(userID).isNotNull();
  }

  /**
   * JUnit test findByProjectID method
   */
  @Test
  @DisplayName("findByProjectID()")
  void givenProjectID_whenFindByProjectID_thenReturnProjectObject() {
    Project expectedProject = projectRepository.findByProjectID(project.getProjectID());
    assertThat(expectedProject).isNotNull();
  }
}