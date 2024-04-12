package com.example.shortlinkapplication.controller;

import static org.mockito.Mockito.when;

import com.example.shortlinkapplication.dto.url.URLRequest;
import com.example.shortlinkapplication.dto.url.UrlDeleteRequest;
import com.example.shortlinkapplication.dto.url.UrlUpdateRequest;
import com.example.shortlinkapplication.entity.Project;
import com.example.shortlinkapplication.entity.Url;
import com.example.shortlinkapplication.entity.User;
import com.example.shortlinkapplication.repository.ProjectRepository;
import com.example.shortlinkapplication.security.UserPrincipal;
import com.example.shortlinkapplication.service.url.UrlServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@WebMvcTest(UrlController.class)
class UrlControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private UrlServiceImpl urlService;
  @MockBean
  private ProjectRepository projectRepository;
  @MockBean
  private ProjectController projectController;
  private User user;
  private UserPrincipal userPrincipal;
  private Project project;
  private Url url;

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

    url = new Url();
    url.setId(1);
    url.setProjectID(project);
    url.setLongUrl("https://hackernoon.com/how-to-shorten-urls-java-and-spring-step-by-step-guide");
    url.setShortUrl("qyDz2a");

    when(projectController.getUser(userPrincipal)).thenReturn(user);
  }

  /**
   * JUnit test GET request - getUrlList method
   */
  @Test
  @DisplayName("getUrlList()")
  void givenProjectID_whenGetUrlList_thenReturnUrlList() throws Exception {
    when(projectRepository.findByProjectID(project.getProjectID())).thenReturn(project);
    when(urlService.getListUrl(project.getProjectID(), user)).thenReturn(List.of(url));

    mockMvc.perform(
            MockMvcRequestBuilders.get("/{projectSlug}/get-url-list", project.getProjectSlug())
                .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                .param("projectID", "1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value(Matchers.hasSize(1)));
  }

  /**
   * JUnit test POST request - convertToShortUrl method
   */
  @Test
  @DisplayName("convertToShortUrl()")
  void givenURLRequest_whenConvertToShortUrl_thenReturnUrlObject() throws Exception {
    URLRequest request = new URLRequest();
    request.setProjectID(1);
    request.setLongUrl(
        "https://hackernoon.com/how-to-shorten-urls-java-and-spring-step-by-step-guide");

    when(urlService.convertToShortUrl(request, user)).thenReturn(url);

    mockMvc.perform(
            MockMvcRequestBuilders.post("/{projectSlug}/create-short", project.getProjectSlug())
                .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.longUrl").value("https://hackernoon"
            + ".com/how-to-shorten-urls-java-and-spring-step-by-step-guide"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.shortUrl").value("qyDz2a"));
  }

  /**
   * JUnit test PUT request - updateLongUrl method
   */
  @Test
  @DisplayName("updateLongUrl()")
  void givenUrlUpdateRequest_whenUpdateLongUrl_thenReturnUrlObject() throws Exception {
    UrlUpdateRequest request = new UrlUpdateRequest();
    request.setId(1);
    request.setLongUrl(
        "https://github.com/cachedinsights/urlshorteningservice/blob/master/src/main/java/com/stackfortech/urlShorteningService/Controller/UrlShorteningController.java");
    request.setShortUrl("qyDz2a");

    url.setId(1);
    url.setLongUrl(request.getLongUrl());
    url.setShortUrl(request.getShortUrl());

    when(urlService.updateLongUrl(request)).thenReturn(url);

    mockMvc.perform(
            MockMvcRequestBuilders.put("/{projectSlug}/update-long-url", project.getProjectSlug())
                .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.longUrl").value("https://github"
            + ".com/cachedinsights/urlshorteningservice/blob/master/src/main/java/com/stackfortech/urlShorteningService/Controller/UrlShorteningController.java"));
  }

  /**
   * JUnit test DELETE request - deleteUrl method
   */
  @Test
  @DisplayName("deleteUrl()")
  void givenUrlDeleteRequest_whenDeleteUrl_thenReturnUrlList() throws Exception {
    Url url1 = new Url();
    url1.setId(2);
    url1.setProjectID(project);
    url1.setLongUrl(
        "https://hackernoon.com/how-to-shorten-urls-java-and-spring-step-by-step-guide");
    url1.setShortUrl("qyDz5n");

    UrlDeleteRequest request = new UrlDeleteRequest();
    request.setProjectID(1);
    request.setShortUrl("qyDz2a");

    when(urlService.deleteUrl(request, user)).thenReturn(List.of(url1));

    mockMvc.perform(
            MockMvcRequestBuilders.delete("/{projectSlug}/delete-url", project.getProjectSlug())
                .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value(Matchers.hasSize(1)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("2"));
  }

  /**
   * JUnit test GET request - getUrlByProjectID method
   */
  @Test
  @DisplayName("getUrlByProjectID()")
  void givenProjectIDAndPageable_whenGetUrlByProjectID_thenUrlPage() throws Exception {
    Pageable pageable = PageRequest.of(0, 10);
    List<Url> urlList = List.of(url);

    when(urlService.findAllUrlByProjectID(project.getProjectID(), pageable)).thenReturn(
        new PageImpl<>(urlList, pageable, urlList.size()));

    mockMvc.perform(MockMvcRequestBuilders.get("/{projectSlug}/urls", project.getProjectSlug())
            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
            .param("projectID", "1")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(Matchers.hasSize(1)));
  }

  /**
   * JUnit test GET request - sortByCreateDate method
   */
  @Test
  @DisplayName("sortByCreateDate()")
  void givenProjectID_whenSortByCreateDate_thenReturnUrlList() throws Exception {
    when(urlService.sortByCreationDate(project.getProjectID())).thenReturn(List.of(url));

    mockMvc.perform(MockMvcRequestBuilders.get("/{projectSlug}/sort-by-create-date",
                project.getProjectSlug())
            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
            .param("projectID", "1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value(Matchers.hasSize(1)));
  }

  /**
   * JUnit test GET request - sortByTotalClick method
   */
  @Test
  @DisplayName("sortByTotalClick()")
  void givenProjectID_whenSortByTotalClick_thenReturnUrlList() throws Exception {
    when(urlService.sortByTotalClick(project.getProjectID())).thenReturn(List.of(url));

    mockMvc.perform(MockMvcRequestBuilders.get("/{projectSlug}/sort-by-total-click-url",
                project.getProjectSlug())
            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
            .param("projectID", "1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value(Matchers.hasSize(1)));
  }

  /**
   * JUnit test GET request - search method
   */
  @Test
  @DisplayName("search()")
  void givenKeyWordString_whenSearch_thenReturnUrlList() throws Exception {
    String keyword = "hackernoon";
    when(urlService.search(keyword)).thenReturn(List.of(url));

    mockMvc.perform(MockMvcRequestBuilders.get("/{projectSlug}/search", project.getProjectSlug())
            .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
            .param("search", keyword))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value(Matchers.hasSize(1)));
  }
}