package com.example.shortlinkapplication.dto.url;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlDeleteRequest {

  Integer projectID;
  String shortUrl;
}
