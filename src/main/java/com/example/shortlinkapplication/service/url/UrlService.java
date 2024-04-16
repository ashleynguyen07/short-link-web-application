package com.example.shortlinkapplication.service.url;

import com.example.shortlinkapplication.dto.url.GuestUrlRequest;
import com.example.shortlinkapplication.dto.url.URLRequest;
import com.example.shortlinkapplication.dto.url.UrlDeleteRequest;
import com.example.shortlinkapplication.entity.Url;
import com.example.shortlinkapplication.dto.url.UrlUpdateRequest;
import com.example.shortlinkapplication.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UrlService {

  List<Url> getListUrl(Integer projectID, User userID);

  Url convertToShortUrl(URLRequest longUrl, User userID);

  Url createShortLink(GuestUrlRequest request);

  String getLongUrl(String shortUrl);

  Url updateLongUrl(UrlUpdateRequest request);

  List<Url> sortByCreationDate(Integer projectID);

  List<Url> sortByTotalClick(Integer projectID);

  List<Url> search(String keyword);

  List<Url> deleteUrl(UrlDeleteRequest shortUrl, User userID);

  Page<Url> findAllUrlByProjectID(Integer projectID, Pageable pageable);
}
