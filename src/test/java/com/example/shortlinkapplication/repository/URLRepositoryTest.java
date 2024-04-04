package com.example.shortlinkapplication.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.shortlinkapplication.entity.Project;
import com.example.shortlinkapplication.entity.Url;
import com.example.shortlinkapplication.entity.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
class URLRepositoryTest {

  @Autowired
  private URLRepository urlRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProjectRepository projectRepository;
  private Project project;
  private Url url;
  private User user;

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

    url = new Url();
    url.setId(1);
    url.setLongUrl("https://github.com/ashleynguyen07");
    url.setShortUrl("ndhXu2");
    url.setProjectID(project);
    urlRepository.save(url);
  }

  /**
   * JUnit test findUrlByProjectID method
   */
  @Test
  @DisplayName("findUrlByProjectID()")
  void givenProjectID_whenFindUrlByProjectID_thenReturnUrlList() {
    Optional<Project> optionalProject = projectRepository.findById(project.getProjectID());
    if (optionalProject.isEmpty()) {
      throw new IllegalStateException("Project not found");
    }
    List<Url> ls = urlRepository.findUrlByProjectID(optionalProject.get());
    assertThat(ls).hasSize(1);
  }

  /**
   * JUnit test findAllByProjectID method
   */
  @Test
  @DisplayName("findAllByProjectID()")
  void givenProjectIDAndPageable_whenFindAllByProjectID_thenReturnUrlPage() {
    Optional<Project> optionalProject = projectRepository.findById(project.getProjectID());
    if (optionalProject.isEmpty()) {
      throw new IllegalStateException("Project not found");
    }

    Pageable pageable = PageRequest.of(0, 2);
    Page<Url> ls = urlRepository.findAllByProjectID(optionalProject.get(), pageable);
    assertThat(ls).isNotNull();
  }

  /**
   * JUnit test existsByShortUrl method
   */
  @Test
  @DisplayName("existsByShortUrl()")
  void givenEmail_whenExistsByShortUrl_thenReturnTrue() {
    Boolean expected = urlRepository.existsByShortUrl(url.getShortUrl());
    assertThat(expected).isTrue();
  }

  /**
   * JUnit test findByShortUrl method
   */
  @Test
  @DisplayName("findByShortUrl()")
  void givenShortUrl_whenFindByShortUrl_thenReturnUrlObject() {
    Optional<Url> expectedUrl = urlRepository.findByShortUrl(url.getShortUrl());
    assertThat(expectedUrl).isNotNull();
  }

  /**
   * JUnit test deleteByShortUrl method
   */
  @Test
  @DisplayName("deleteByShortUrl()")
  void givenShortUrl_whenDeleteByShortUrl_thenReturnNothing() {
    urlRepository.deleteByShortUrl(url.getShortUrl());

    // after delete shortUrl must be null
    assertThat(urlRepository.findByShortUrl(url.getShortUrl())).isEmpty();
  }

  /**
   * JUnit test findByProjectIDOrderByCreationDateDesc method
   */
  @Test
  @DisplayName("findByProjectIDOrderByCreationDateDesc()")
  void givenProjectID_whenFindByProjectIDOrderByCreationDateDesc_thenReturnUrlList() {
    Optional<Project> optionalProject = projectRepository.findById(project.getProjectID());
    if (optionalProject.isEmpty()) {
      throw new IllegalStateException("Project is not found with ID: " + project.getProjectID());
    }
    List<Url> ls = urlRepository.findByProjectIDOrderByCreationDateDesc(optionalProject.get());
    assertThat(ls).hasSize(1);
  }

  /**
   * JUnit test findByProjectOrderByTotalClickUrlDesc method
   */
  @Test
  @DisplayName("findByProjectIDOrderByTotalClickUrlDesc()")
  void givenProjectID_whenFindByProjectIDOrderByTotalClickUrlDesc_thenReturnUrlList() {
    Optional<Project> optionalProject = projectRepository.findById(project.getProjectID());
    if (optionalProject.isEmpty()) {
      throw new IllegalStateException("Project is not found with ID: " + project.getProjectID());
    }
    List<Url> ls = urlRepository.findByProjectIDOrderByTotalClickUrlDesc(optionalProject.get());
    assertThat(ls).hasSize(1);
  }

  /**
   * JUnit test search method
   */
  @Test
  @DisplayName("search()")
  void givenKeyword_whenSearch_thenReturnUrlList() {
    List<Url> ls = urlRepository.search("ndhXu2");
    assertThat(ls).hasSize(1);
  }
}