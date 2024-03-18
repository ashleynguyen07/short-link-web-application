package com.example.shortlinkapplication.repository;

import com.example.shortlinkapplication.entity.Project;
import com.example.shortlinkapplication.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

  List<Project> findProjectByUserID(User userID);

  @Transactional
  @Query("SELECT userID FROM Project c " +
      "WHERE c.projectID = ?1")
  User findUserIDByProjectID(Integer projectID);

  Project findByProjectID(Integer projectID);
}
