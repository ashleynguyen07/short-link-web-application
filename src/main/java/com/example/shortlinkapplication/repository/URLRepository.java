package com.example.shortlinkapplication.repository;

import com.example.shortlinkapplication.entity.Project;
import com.example.shortlinkapplication.entity.Url;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface URLRepository extends JpaRepository<Url, Long> {

  List<Url> findUrlByProjectID(Project projectID);

  boolean existsByShortUrl(String shortUrl);

  Optional<Url> findByShortUrl(String shortUrl);

  @Transactional
  @Modifying
  @Query("DELETE FROM Url u WHERE u.shortUrl = ?1")
  void deleteByShortUrl(String shortUrl);
}
