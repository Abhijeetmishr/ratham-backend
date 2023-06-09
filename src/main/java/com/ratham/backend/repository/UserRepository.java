package com.ratham.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ratham.backend.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUniversityId(String universityId);
}