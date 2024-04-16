package com.example.shortlinkapplication.controller;

import com.example.shortlinkapplication.dto.url.GuestUrlRequest;
import com.example.shortlinkapplication.entity.Url;
import com.example.shortlinkapplication.service.url.UrlServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicUrlController {

  private static final Logger logger = LoggerFactory.getLogger(PublicUrlController.class);
  private final UrlServiceImpl urlService;

  @GetMapping(value = "{shortUrl}")
  @Cacheable(value = "urls", key = "#shortUrl", sync = true)
  public RedirectView getAndRedirect(@PathVariable String shortUrl) {
    logger.info("redirect long url is running....");
    String longUrl = urlService.getLongUrl(shortUrl);
    return new RedirectView(longUrl);
  }

  @PostMapping("/guest-create-url")
  public Url guestCreateShortUrl(@RequestBody GuestUrlRequest request) {
    return urlService.createShortLink(request);
  }
}
