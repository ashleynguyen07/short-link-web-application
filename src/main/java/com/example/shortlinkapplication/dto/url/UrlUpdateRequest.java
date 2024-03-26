package com.example.shortlinkapplication.dto.url;

import lombok.Data;

@Data
public class UrlUpdateRequest {

  Integer id;
  String longUrl;
  String shortUrl;

}
