package com.example.customer_api.dto;

import com.example.customer_api.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoleDTO {
    @NotNull
    private Role role;
}

