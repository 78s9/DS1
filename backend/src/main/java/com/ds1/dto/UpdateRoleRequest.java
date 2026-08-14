package com.ds1.dto;

import javax.validation.constraints.NotBlank;

public class UpdateRoleRequest {

    @NotBlank(message = "角色不能为空")
    private String role;

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
