package com.example.shortlinkapplication.dto.url;

import lombok.Data;

@Data
public class UrlDeleteRequest {

  Integer projectID;
  String shortUrl;
}
