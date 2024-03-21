package com.example.shortlinkapplication.repository;

import com.example.shortlinkapplication.entity.Project;
import com.example.shortlinkapplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    List<Project> findProjectByUserID(User userID);
    //Optional<Project> findSlugByProjectID(Integer projectID);
}
