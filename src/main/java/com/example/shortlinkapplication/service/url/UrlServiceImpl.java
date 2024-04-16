package com.example.shortlinkapplication.service.url;

import com.example.shortlinkapplication.dto.url.GuestUrlRequest;
import com.example.shortlinkapplication.dto.url.URLRequest;
import com.example.shortlinkapplication.dto.url.UrlDeleteRequest;
import com.example.shortlinkapplication.dto.url.UrlUpdateRequest;
import com.example.shortlinkapplication.entity.Project;
import com.example.shortlinkapplication.entity.Url;
import com.example.shortlinkapplication.entity.User;
import com.example.shortlinkapplication.repository.ProjectRepository;
import com.example.shortlinkapplication.repository.URLRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

  private static final Logger logger = LoggerFactory.getLogger(UrlServiceImpl.class);
  private final URLRepository urlRepository;
  private final ProjectRepository projectRepository;
  private final BaseConversion baseConversion;

  /**
   * get list url by projectID from request
   *
   * @return urlList
   */
  @Override
  public List<Url> getListUrl(Integer projectID, User userID) {
    User findUserID = projectRepository.findUserIDByProjectID(projectID);
    if (!userID.equals(findUserID)) {
      logger.error("Handler policy user request");
      return Collections.emptyList();
    } else {
      Project project = projectRepository.findByProjectID(projectID);
      List<Url> urlList = urlRepository.findUrlByProjectID(project);
      if (urlList.isEmpty()) {
        logger.error("Url list is null");
        return new ArrayList<>();
      }
      logger.info("Url list: {}", urlList);
      return urlList;
    }
  }

  /**
   * generate long url to short url
   *
   * @return shortUrl
   */
  @Override
  public Url convertToShortUrl(URLRequest request, User userID) {
    User findUserID = projectRepository.findUserIDByProjectID(request.getProjectID());
    if (!userID.equals(findUserID)) {
      logger.error("Handler policy user request");
      return null;
    } else {
      var url = new Url();
      url.setLongUrl(request.getLongUrl());
      url.setCreationDate(LocalDate.now());

      Optional<Project> optionalProject = projectRepository.findById(request.getProjectID());
      if (optionalProject.isPresent()) {
        Project project = optionalProject.get();
        url.setProjectID(project);
      }

      var entity = urlRepository.save(url);

      String encodeString = baseConversion.encode(String.valueOf(entity.getId()));
      logger.info("Encode string: {}", encodeString);

      StringBuilder shortUrl = new StringBuilder(encodeString.substring(0, 6));

      while (urlRepository.existsByShortUrl(String.valueOf(shortUrl))) {
        encodeString = encodeString.substring(6);
        shortUrl.append(encodeString, 0, 6);
      }

      url.setShortUrl(String.valueOf(shortUrl));
      urlRepository.save(url);

      logger.info("Full domain: {}", shortUrl);
      return url;
    }
  }

  @Override
  public Url createShortLink(GuestUrlRequest request) {
    Url url = new Url();
    url.setLongUrl(request.getLongUrl());
    url.setCreationDate(LocalDate.now());
    url.setProjectID(null);
    url.setExpirationDate(LocalDate.now().plusDays(1));

    var entity = urlRepository.save(url);

    String encodeString = baseConversion.encode(String.valueOf(entity.getId()));
    logger.info("Encode string: {}", encodeString);

    StringBuilder shortUrl = new StringBuilder(encodeString.substring(0, 6));

    while (urlRepository.existsByShortUrl(String.valueOf(shortUrl))) {
      encodeString = encodeString.substring(6);
      shortUrl.append(encodeString, 0, 6);
    }

    url.setShortUrl(String.valueOf(shortUrl));
    urlRepository.save(url);
    return url;
  }

  @Override
  public String getLongUrl(String shortUrl) {
    Optional<Url> optionalUrl = urlRepository.findByShortUrl(shortUrl);
    logger.info("Short url: {}", optionalUrl);

    if (optionalUrl.isPresent()) {
      Url url = optionalUrl.get();
      logger.info("Get long url: {}", url.getLongUrl());

      // count click url
      Integer totalClick = url.getTotalClickUrl();
      if (totalClick != null) {
        url.setTotalClickUrl(totalClick + 1);
      } else {
        url.setTotalClickUrl(1);
      }
      urlRepository.save(url);
      logger.info("Total click: {}", url.getTotalClickUrl());

      return url.getLongUrl();
    }
    return null;
  }

  @Override
  public Url updateLongUrl(UrlUpdateRequest request) {
    Optional<Url> optionalUrl = urlRepository.findById(Long.valueOf(request.getId()));
    logger.info("Url: {}", optionalUrl);

    if (optionalUrl.isPresent()) {
      Url url = optionalUrl.get();
      url.setLongUrl(request.getLongUrl());
      urlRepository.save(url);

      return url;
    }
    throw new IllegalArgumentException("Url not found with id: " + request.getId());
  }

  @Override
  public List<Url> sortByCreationDate(Integer projectID) {
    Project project = projectRepository.findByProjectID(projectID);
    logger.info("ProjectID: {}", projectID);
    return urlRepository.findByProjectIDOrderByCreationDateDesc(project);
  }

  @Override
  public List<Url> deleteUrl(UrlDeleteRequest request, User userID) {
    urlRepository.deleteByShortUrl(request.getShortUrl());
    List<Url> urlList = getListUrl(request.getProjectID(), userID);
    logger.info("Url list: {}", urlList);
    return urlList;
  }

  @Override
  public Page<Url> findAllUrlByProjectID(Integer projectID, Pageable pageable) {
    Project project = projectRepository.findByProjectID(projectID);
    return urlRepository.findAllByProjectID(project, pageable);
  }

  @Override
  public List<Url> sortByTotalClick(Integer projectID) {
    Project project = projectRepository.findByProjectID(projectID);
    return urlRepository.findByProjectIDOrderByTotalClickUrlDesc(project);
  }

  @Override
  public List<Url> search(String keyword) {
    if (keyword != null) {
      return urlRepository.search(keyword);
    }
    return urlRepository.findAll();
  }

}
