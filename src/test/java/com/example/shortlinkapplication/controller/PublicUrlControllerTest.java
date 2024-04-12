package com.example.shortlinkapplication.controller;

import static org.mockito.Mockito.when;

import com.example.shortlinkapplication.entity.Url;
import com.example.shortlinkapplication.service.url.UrlServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PublicUrlController.class)
class PublicUrlControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private UrlServiceImpl urlService;
  private Url url;

  @BeforeEach
  void setUp() {
    url = new Url();
    url.setId(1);
    url.setLongUrl("https://hackernoon.com/how-to-shorten-urls-java-and-spring-step-by-step-guide");
    url.setShortUrl("qyDz2a");
  }

  /**
   * JUnit test GET request - getAndRedirect method
   */
  @Test
  @DisplayName("getAndRedirect()")
  @WithMockUser
  void givenShortUrl_whenGetAndRedirect_thenReturnLongUrl() throws Exception {
    when(urlService.getLongUrl(url.getShortUrl())).thenReturn(url.getLongUrl());

    mockMvc.perform(MockMvcRequestBuilders.get("/public/{shortUrl}", url.getShortUrl()))
        .andExpect(MockMvcResultMatchers.status().isFound())
        .andExpect(MockMvcResultMatchers.redirectedUrl(url.getLongUrl()));
  }
}