package com.example.shortlinkapplication.repository;

import com.example.shortlinkapplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

  User findByEmail(String email);

  Boolean existsByEmail(String email);
}
