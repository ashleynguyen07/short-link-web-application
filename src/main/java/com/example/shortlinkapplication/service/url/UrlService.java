package com.example.shortlinkapplication.service.url;

import com.example.shortlinkapplication.dto.url.URLRequest;
import com.example.shortlinkapplication.dto.url.UrlDeleteRequest;
import com.example.shortlinkapplication.entity.Url;
import com.example.shortlinkapplication.dto.url.UrlUpdateRequest;
import com.example.shortlinkapplication.entity.User;
import java.util.List;

public interface UrlService {

  List<Url> getListUrl(Integer projectID, User userID);

  Url convertToShortUrl(URLRequest longUrl, User userID);

  String getLongUrl(String shortUrl);

  Url updateLongUrl(UrlUpdateRequest request);

  List<Url> deleteUrl(UrlDeleteRequest shortUrl, User userID);
}
