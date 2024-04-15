package com.example.shortlinkapplication.dto.url;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlUpdateRequest {

  Integer id;
  String longUrl;
  String shortUrl;

}
