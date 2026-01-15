package com.example.customer_api.repository;

import com.example.customer_api.dto.LoginResponseDTO;
import com.example.customer_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByEmail(String email);

    Optional<User> findByResetToken(String resetToken);
}
