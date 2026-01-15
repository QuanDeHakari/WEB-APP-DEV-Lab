package com.example.customer_api.service;

import com.example.customer_api.dto.*;
import com.example.customer_api.entity.RefreshToken;
import com.example.customer_api.entity.Role;
import com.example.customer_api.entity.User;
import com.example.customer_api.exception.DuplicateResourceException;
import com.example.customer_api.exception.ResourceNotFoundException;
import com.example.customer_api.repository.RefreshTokenRepository;
import com.example.customer_api.repository.UserRepository;
import com.example.customer_api.security.JwtTokenProvider;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // create and save refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(refreshToken);

        return new LoginResponseDTO(
                accessToken,
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                refreshToken.getToken()
        );
    }
    
    @Override
    public UserResponseDTO register(RegisterRequestDTO registerRequest) {
        // Check if username exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setRole(Role.USER);  // Default role
        user.setIsActive(true);
        
        User savedUser = userRepository.save(user);
        
        return convertToDTO(savedUser);
    }
    
    @Override
    public UserResponseDTO getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return convertToDTO(user);
    }
    
    private UserResponseDTO convertToDTO(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFullName(),
            user.getRole().name(),
            user.getIsActive(),
            user.getCreatedAt()
        );
    }

    @Override
    public void changePassword(ChangePasswordDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        return token;
    }

    @Override
    public void resetPassword(ResetPasswordDTO dto) {
        User user = userRepository.findByResetToken(dto.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (user.getResetTokenExpiry() == null ||
                user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token is expired");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    @Override
    public UserResponseDTO getCurrentUserProfile() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());

        return dto;
    }

    @Override
    public UserResponseDTO updateCurrentUserProfile(UpdateProfileDTO dto) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());

        User saved = userRepository.save(user);

        UserResponseDTO response = new UserResponseDTO();
        response.setId(saved.getId());
        response.setUsername(saved.getUsername());
        response.setFullName(saved.getFullName());
        response.setEmail(saved.getEmail());

        return response;
    }

    @Override
    public void deleteCurrentAccount(String password) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Password is incorrect");
        }

        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream().map(user -> {
            UserResponseDTO dto = new UserResponseDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setFullName(user.getFullName());
            dto.setEmail(user.getEmail());
            dto.setIsActive(user.getIsActive());
            return dto;
        }).toList();
    }

    @Override
    public UserResponseDTO updateUserRole(Long id, UpdateRoleDTO dto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setRole(dto.getRole());

        User saved = userRepository.save(user);

        UserResponseDTO response = new UserResponseDTO();
        response.setId(saved.getId());
        response.setUsername(saved.getUsername());
        response.setFullName(saved.getFullName());
        response.setEmail(saved.getEmail());
        response.setIsActive(saved.getIsActive());
        response.setRole(saved.getRole().toString());

        return response;
    }

    @Override
    public UserResponseDTO toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setIsActive(!user.getIsActive());   // flip boolean
        User saved = userRepository.save(user);

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(saved.getId());
        dto.setUsername(saved.getUsername());
        dto.setFullName(saved.getFullName());
        dto.setEmail(saved.getEmail());
        dto.setIsActive(saved.getIsActive());

        return dto;
    }

    @Override
    public LoginResponseDTO refreshToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        User user = refreshToken.getUser();

        String newAccessToken = tokenProvider.generateTokenFromUsername(user.getUsername());

        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(newAccessToken);
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setRefreshToken(refreshTokenValue);

        return response;
    }
}

