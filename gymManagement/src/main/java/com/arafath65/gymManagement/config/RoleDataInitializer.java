package com.arafath65.gymManagement.config;

import com.arafath65.gymManagement.entity.Role;
import com.arafath65.gymManagement.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoleDataInitializer {

    @Bean
    CommandLineRunner initializeRoles(
            RoleRepository roleRepository) {

        return args -> {

            createRole(
                    roleRepository,
                    "SUPER_ADMIN",
                    "FitFloor system administrator"
            );

            createRole(
                    roleRepository,
                    "GYM_OWNER",
                    "Gym owner with tenant-level access"
            );

            createRole(
                    roleRepository,
                    "BRANCH_MANAGER",
                    "Manager responsible for a branch"
            );

            createRole(
                    roleRepository,
                    "RECEPTION",
                    "Reception staff"
            );

            createRole(
                    roleRepository,
                    "CASHIER",
                    "Cashier responsible for payments"
            );

            createRole(
                    roleRepository,
                    "TRAINER",
                    "Gym trainer"
            );

            createRole(
                    roleRepository,
                    "ACCOUNTANT",
                    "Accountant responsible for financial operations"
            );
        };
    }

    private void createRole(
            RoleRepository roleRepository,
            String roleName,
            String description) {

        if (!roleRepository.existsByRoleName(roleName)) {

            Role role = new Role();

            role.setRoleName(roleName);
            role.setDescription(description);
            role.setStatus(true);

            roleRepository.save(role);
        }
    }
}