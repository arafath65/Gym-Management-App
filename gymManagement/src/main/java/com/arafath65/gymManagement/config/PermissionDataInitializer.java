package com.arafath65.gymManagement.config;

import com.arafath65.gymManagement.entity.Permission;
import com.arafath65.gymManagement.repository.PermissionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PermissionDataInitializer {
    @Bean
    CommandLineRunner initializePermissionCatalogue(PermissionRepository repository) {
        return args -> {
            add(repository, "DASHBOARD_OVERVIEW_VIEW", "DASHBOARD", "Overview", "VIEW");
            String[][] definitions = {
                {"MEMBERS", "LIST", "Member List", "VIEW,CREATE,EDIT,DELETE,EXPORT"},
                {"MEMBERS", "PROFILE", "Member Profile", "VIEW,EDIT,DEACTIVATE"},
                {"MEMBERSHIP", "MANAGEMENT", "Membership Management", "VIEW,ASSIGN,EDIT,RENEW,FREEZE,UNFREEZE,CANCEL"},
                {"PROGRAMS", "MANAGEMENT", "Programs", "VIEW,CREATE,EDIT,DELETE,ENROLL"},
                {"BENEFITS", "MANAGEMENT", "Benefits", "VIEW,CREATE,EDIT,DELETE,ASSIGN"},
                {"PAYMENTS", "COLLECTION", "Payments", "VIEW,COLLECT,EDIT,REFUND,VOID,PRINT_RECEIPT,EXPORT"},
                {"MONTHLY_DUES", "MANAGEMENT", "Monthly Dues", "VIEW,GENERATE,WAIVE,COLLECT"},
                {"ATTENDANCE", "MANAGEMENT", "Attendance", "VIEW,CHECK_IN,CHECK_OUT,EDIT,DELETE,EXPORT"},
                {"TRAINERS", "MANAGEMENT", "Trainers", "VIEW,CREATE,EDIT,DELETE"},
                {"BRANCHES", "MANAGEMENT", "Branches", "VIEW,CREATE,EDIT,DEACTIVATE"},
                {"USERS", "MANAGEMENT", "Users", "VIEW,CREATE,EDIT,DEACTIVATE,RESET_PASSWORD,ASSIGN_ROLE"},
                {"ROLES", "MANAGEMENT", "Role Management", "VIEW,CREATE,EDIT,DELETE,MANAGE_PERMISSIONS"},
                {"AUDIT_LOGS", "LOG_VIEWER", "Audit Logs", "VIEW,EXPORT"},
                {"REPORTS", "REPORT_VIEWER", "Reports", "VIEW,EXPORT"},
                {"SETTINGS", "GENERAL", "Settings", "VIEW,EDIT"}
            };
            for (String[] d : definitions) {
                for (String action : d[3].split(",")) {
                    add(repository, d[0] + "_" + d[1] + "_" + action, d[0], d[2], action);
                }
            }
        };
    }

    private void add(PermissionRepository repository, String code, String module, String screen, String action) {
        if (repository.findByPermissionCode(code).isEmpty()) {
            Permission p = new Permission();
            p.setPermissionCode(code);
            p.setModule(module);
            p.setScreen(screen);
            p.setAction(action);
            p.setDescription(screen + " - " + action.replace('_', ' ').toLowerCase());
            p.setStatus(true);
            repository.save(p);
        }
    }
}
