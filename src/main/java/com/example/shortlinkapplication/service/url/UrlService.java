package com.example.shortlinkapplication.service.url;

import com.example.shortlinkapplication.dto.url.URLRequest;
import com.example.shortlinkapplication.dto.url.UrlDeleteRequest;
import com.example.shortlinkapplication.entity.Url;
import com.example.shortlinkapplication.dto.url.UrlUpdateRequest;
import java.util.List;

public interface UrlService {

  List<Url> getListUrl(Integer projectID);

  Url convertToShortUrl(URLRequest longUrl);

  String getLongUrl(String shortUrl);

  Url updateLongUrl(UrlUpdateRequest request);

  List<Url> deleteUrl(UrlDeleteRequest shortUrl);

  List<Url> sortByCreationDate(Integer projectID);

  List<Url> sortByTotalClick(Integer projectID);

  List<Url> search(String keyword);

}
