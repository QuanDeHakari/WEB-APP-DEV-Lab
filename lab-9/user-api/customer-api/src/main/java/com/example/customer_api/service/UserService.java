package com.example.customer_api.service;

import com.example.customer_api.dto.ChangePasswordDTO;
import com.example.customer_api.dto.LoginRequestDTO;
import com.example.customer_api.dto.LoginResponseDTO;
import com.example.customer_api.dto.RegisterRequestDTO;
import com.example.customer_api.dto.ResetPasswordDTO;
import com.example.customer_api.dto.UpdateProfileDTO;
import com.example.customer_api.dto.UpdateRoleDTO;
import com.example.customer_api.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    
    LoginResponseDTO login(LoginRequestDTO loginRequest);
    
    UserResponseDTO register(RegisterRequestDTO registerRequest);
    
    UserResponseDTO getCurrentUser(String username);

    void changePassword(ChangePasswordDTO dto);

    String forgotPassword(String email);

    void resetPassword(ResetPasswordDTO dto);

    UserResponseDTO getCurrentUserProfile();

    UserResponseDTO updateCurrentUserProfile(UpdateProfileDTO dto);

    void deleteCurrentAccount(String password);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO updateUserRole(Long id, UpdateRoleDTO dto);

    UserResponseDTO toggleUserStatus(Long id);
    
    LoginResponseDTO refreshToken(String refreshTokenValue);
}
