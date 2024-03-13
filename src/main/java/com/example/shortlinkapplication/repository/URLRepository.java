package com.example.shortlinkapplication.repository;

import com.example.shortlinkapplication.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface URLRepository extends JpaRepository<Url, Long> {
    boolean existsByShortUrl (String shortUrl);
    Url findUrlByShortUrl (String shortUrl);
    Url findUrlByLongUrl (String originalUrl);
    boolean existsByLongUrl (String originalUrl);
}
