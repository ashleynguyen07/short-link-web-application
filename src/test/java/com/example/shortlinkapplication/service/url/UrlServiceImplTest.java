package com.example.shortlinkapplication.service.url;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.example.shortlinkapplication.dto.url.URLRequest;
import com.example.shortlinkapplication.dto.url.UrlDeleteRequest;
import com.example.shortlinkapplication.dto.url.UrlUpdateRequest;
import com.example.shortlinkapplication.entity.Project;
import com.example.shortlinkapplication.entity.Url;
import com.example.shortlinkapplication.entity.User;
import com.example.shortlinkapplication.repository.ProjectRepository;
import com.example.shortlinkapplication.repository.URLRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

  @Mock
  private URLRepository urlRepository;
  @Mock
  private ProjectRepository projectRepository;
  @Mock
  private BaseConversion baseConversion;
  @InjectMocks
  private UrlServiceImpl urlService;
  private User user;
  private Project project;
  private Url url;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setUserID(1);
    user.setName("My");
    user.setEmail("nguyentramy.se@gmail.com");

    project = new Project();
    project.setProjectID(1);
    project.setProjectName("short");
    project.setProjectSlug("short");
    project.setUserID(user);

    url = new Url();
    url.setId(1);
    url.setProjectID(project);
    url.setLongUrl("longUrl");
    url.setShortUrl("shortU");
    url.setCreationDate(LocalDate.now());
  }

  /**
   * JUnit test getListUrl method then return url list
   */
  @Test
  @DisplayName("getListUrl()")
  void givenProjectIDAndUserID_whenGetListUrl_thenReturnUrlList() {
    when(projectRepository.findUserIDByProjectID(project.getProjectID())).thenReturn(user);
    when(projectRepository.findByProjectID(project.getProjectID())).thenReturn(project);
    when(urlRepository.findUrlByProjectID(project)).thenReturn(List.of(url));

    List<Url> urlList = urlService.getListUrl(project.getProjectID(), user);
    assertThat(urlList).hasSize(1);
  }

  /**
   * JUnit test getListUrl method with non-exist user
   */
  @Test
  @DisplayName("getListUrl() with non-exist user")
  void givenProjectID_whenGetListUrl_thenReturnEmptyList() {
    when(projectRepository.findUserIDByProjectID(project.getProjectID())).thenReturn(user);
    User nonExistUser = new User();
    nonExistUser.setUserID(2);
    List<Url> urlList = urlService.getListUrl(project.getProjectID(), nonExistUser);

    assertThat(urlList).isEqualTo(Collections.emptyList());
  }

  /**
   * JUnit test getListUrl method with empty url list
   */
  @Test
  @DisplayName("getListUrl() with empty url list")
  void givenProjectID_whenGetListUrl_thenReturnNewArrayList() {
    when(projectRepository.findUserIDByProjectID(project.getProjectID())).thenReturn(user);
    when(projectRepository.findByProjectID(project.getProjectID())).thenReturn(project);
    when(urlRepository.findUrlByProjectID(project)).thenReturn(Collections.emptyList());
    List<Url> urlList = urlService.getListUrl(project.getProjectID(), user);

    assertThat(urlList).isEmpty();
  }

  /**
   * JUnit test convertToShortUrl method
   */
  @Test
  @DisplayName("convertToShortUrl()")
  void givenUrlRequest_whenConvertToShortUrl_thenReturnUrlObject() {
    // Prepare test data
    URLRequest request = new URLRequest();
    request.setProjectID(1);
    request.setLongUrl("longUrl");

    // Mock repository behavior
    when(projectRepository.findUserIDByProjectID(anyInt())).thenReturn(user);
    when(projectRepository.findById(project.getProjectID())).thenReturn(Optional.of(project));
    when(urlRepository.save(any(Url.class))).thenReturn(url);
    when(baseConversion.encode(anyString())).thenReturn("nonEmptyEncodeString");
    when(urlRepository.existsByShortUrl(anyString())).thenReturn(true, false);

    // Invoke the method
    Url result = urlService.convertToShortUrl(request, user);

    // Verify behavior
    assertThat(result).isNotNull();
    assertEquals(result.getShortUrl(), "nonEmptyEnco");
    if (url.getTotalClickUrl() != null) {
      assertEquals(url.getTotalClickUrl() + 1, result.getTotalClickUrl());
    } else {
      assertNull(result.getTotalClickUrl());
    }
  }

  /**
   * JUnit test convertToShortUrl method with non-exist user
   */
  @Test
  @DisplayName("convertToShortUrl() with non-exist user")
  void givenProjectID_whenConvertToShortUrl_thenReturnEmptyList() {
    User user2 = new User();
    when(projectRepository.findUserIDByProjectID(project.getProjectID())).thenReturn(user2);
    User nonExistUser = new User();
    nonExistUser.setUserID(2);
    List<Url> urlList = urlService.getListUrl(project.getProjectID(), nonExistUser);

    assertThat(urlList).isEqualTo(Collections.emptyList());
  }

  /**
   * JUnit test getLongUrl method
   */
  @Test
  @DisplayName("getLongUrl()")
  void givenShortUrl_whenGetLongUrl_thenReturnLongUrl() {
    when(urlRepository.findByShortUrl(url.getShortUrl())).thenReturn(Optional.of(url));
    String longUrl = urlService.getLongUrl(url.getShortUrl());

    assertThat(longUrl).isNotNull();
    assertEquals(longUrl, url.getLongUrl());
  }

  /**
   * JUnit test getLongUrl method with non-exist shortUrl
   */
  @Test
  @DisplayName("getLongUrl() with non-exist shortUrl")
  void givenShortUrl_whenGetLongUrl_thenReturnNull() {
    when(urlRepository.findByShortUrl(url.getShortUrl())).thenReturn(Optional.empty());
    String longUrl = urlService.getLongUrl(url.getShortUrl());

    assertThat(longUrl).isNull();
  }

  /**
   * JUnit test updateLongUrl method
   */
  @Test
  @DisplayName("updateLongUrl()")
  void givenUrlUpdateRequest_whenUpdateLongUrl_thenReturnUrlObject() {
    UrlUpdateRequest request = new UrlUpdateRequest();
    request.setId(1);
    request.setShortUrl("shortU");
    request.setLongUrl("updateLongUrl");

    when(urlRepository.findById(Long.valueOf(request.getId()))).thenReturn(Optional.of(url));
    Url result = urlService.updateLongUrl(request);

    assertThat(result).isNotNull();
    assertThat(result.getLongUrl()).isEqualTo("updateLongUrl");
  }

  /**
   * JUnit test updateLongUrl method with non-exist url
   */
  @Test
  @DisplayName("updateLongUrl() with non-exist url")
  void givenUrlUpdateRequest_whenUpdateLongUrl_thenThrowsException() {
    UrlUpdateRequest request = new UrlUpdateRequest();
    request.setId(1);
    request.setShortUrl("shortU");
    request.setLongUrl("updateLongUrl");

    when(urlRepository.findById(Long.valueOf(request.getId()))).thenReturn(Optional.empty());
    assertThrows(IllegalArgumentException.class, () -> {
      urlService.updateLongUrl(request);
    });
  }

  /**
   * JUnit test sortByCreationDate method
   */
  @Test
  @DisplayName("sortByCreationDate()")
  void givenProjectID_whenSortByCreationDate_thenReturnListUrl() {
    when(projectRepository.findByProjectID(project.getProjectID())).thenReturn(project);
    when(urlRepository.findByProjectIDOrderByCreationDateDesc(project)).thenReturn(List.of(url));
    List<Url> result = urlService.sortByCreationDate(project.getProjectID());
    assertThat(result).hasSize(1);
  }

  /**
   * JUnit test deleteUrl method
   */
  @Test
  @DisplayName("deleteUrl()")
  void givenUrlDeleteRequest_whenDeleteUrl_thenReturnUrlList() {
    UrlDeleteRequest request = new UrlDeleteRequest();
    request.setProjectID(1);
    request.setShortUrl("shortU");
    List<Url> result = urlService.deleteUrl(request, user);
    assertThat(result).isEmpty();
  }

  /**
   * JUnit test findAllUrlByProjectID method
   */
  @Test
  @DisplayName("findAllUrlByProjectID()")
  void givenProjectIDAndPageable_whenFindAllUrlByProjectID_thenReturnUrlPage() {
    Pageable pageable = PageRequest.of(0, 2);
    when(projectRepository.findByProjectID(project.getProjectID())).thenReturn(project);
    when(urlRepository.findAllByProjectID(project, pageable)).thenReturn(
        new PageImpl<>(List.of(url), pageable, List.of(url).size()));
    Page<Url> result = urlService.findAllUrlByProjectID(project.getProjectID(), pageable);
    assertThat(result).hasSize(1);
  }

  /**
   * JUnit test sortByTotalClick method
   */
  @Test
  @DisplayName("sortByTotalClick")
  void givenProjectID_whenSortByTotalClick_thenReturnListUrl() {
    when(projectRepository.findByProjectID(project.getProjectID())).thenReturn(project);
    when(urlRepository.findByProjectIDOrderByTotalClickUrlDesc(project)).thenReturn(List.of(url));
    List<Url> result = urlService.sortByTotalClick(project.getProjectID());
    assertThat(result).hasSize(1);
  }

  /**
   * JUnit test search method
   */
  @Test
  @DisplayName("search()")
  void givenKeywordString_whenSearch_thenReturnUrlList() {
    String keyword = "short";
    when(urlRepository.search(keyword)).thenReturn(List.of(url));
    List<Url> result = urlService.search(keyword);
    assertThat(result).hasSize(1);
  }

  /**
   * JUnit test search method with null keyword
   */
  @Test
  @DisplayName("search() with null keyword")
  void givenNullKeywordString_whenSearch_thenReturnUrlList() {
    when(urlRepository.findAll()).thenReturn(List.of(url));
    List<Url> result = urlService.search(null);
    assertThat(result).hasSize(1);
  }
}