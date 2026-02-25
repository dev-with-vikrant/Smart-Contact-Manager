package com.smart.smartcontactmanager.repositories;


import com.smart.smartcontactmanager.entities.user;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<user, Integer> {
    Optional<user> findByEmail(String email);
}