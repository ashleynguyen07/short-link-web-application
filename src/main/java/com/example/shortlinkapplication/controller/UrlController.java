package com.example.shortlinkapplication.controller;

import com.example.shortlinkapplication.dto.url.URLRequest;
import com.example.shortlinkapplication.dto.url.UrlDeleteRequest;
import com.example.shortlinkapplication.dto.url.UrlUpdateRequest;
import com.example.shortlinkapplication.entity.Project;
import com.example.shortlinkapplication.entity.Url;
import com.example.shortlinkapplication.repository.ProjectRepository;
import com.example.shortlinkapplication.service.project.ProjectServiceImpl;
import com.example.shortlinkapplication.service.url.UrlServiceImpl;
import java.util.List;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/{projectSlug}")
public class UrlController {

  private static final Logger logger = LoggerFactory.getLogger(UrlController.class);
  private final UrlServiceImpl urlService;
  private final ProjectServiceImpl projectService;
  private final ProjectRepository projectRepository;

  @GetMapping("/get-url-list")
  public List<Url> getUrlList(@PathVariable String projectSlug, @RequestParam Integer projectID) {
    logger.info("ProjectID: {}", projectID);

    Project project = projectRepository.findByProjectID(projectID);
    projectSlug = project.getProjectSlug();

    logger.info("Slug: {}", projectSlug);
    return urlService.getListUrl(projectID);
  }

  @PostMapping("create-short")
  public Url convertToShortUrl(@RequestBody URLRequest request) {
    return urlService.convertToShortUrl(request);
  }

  @PutMapping("update-long-url")
  public Url updateLongUrl(@RequestBody UrlUpdateRequest request) {
    return urlService.updateLongUrl(request);
  }

  @DeleteMapping("delete-url")
  public List<Url> deleteUrl(@RequestBody UrlDeleteRequest request) {
    return urlService.deleteUrl(request);
  }

  @GetMapping("sort-by-create-date")
  public List<Url> sortByCreateDate(@RequestParam Integer projectID) {
    return urlService.sortByCreationDate(projectID);
  }

  @GetMapping("sort-by-total-click-url")
  public List<Url> sortByTotalClick(@RequestParam Integer projectID) {
    return urlService.sortByTotalClick(projectID);
  }

  @GetMapping("search")
  public List<Url> search(@RequestParam(value = "search") String keyword) {
    return urlService.search(keyword);
  }
}
