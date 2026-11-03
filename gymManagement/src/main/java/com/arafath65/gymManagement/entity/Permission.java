package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "permissions", uniqueConstraints = @UniqueConstraint(name = "uk_permissions_code", columnNames = "permission_code"))
public class Permission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Integer permissionId;

    @Column(name = "permission_code", nullable = false, length = 120)
    private String permissionCode;

    @Column(name = "module_name", nullable = false, length = 80)
    private String module;

    @Column(name = "screen_name", length = 100)
    private String screen;

    @Column(name = "action_name", nullable = false, length = 80)
    private String action;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    public Integer getPermissionId() { return permissionId; }
    public String getPermissionCode() { return permissionCode; }
    public void setPermissionCode(String permissionCode) { this.permissionCode = permissionCode; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public String getScreen() { return screen; }
    public void setScreen(String screen) { this.screen = screen; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }
}
