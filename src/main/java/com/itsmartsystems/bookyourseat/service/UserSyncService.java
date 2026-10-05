package com.itsmartsystems.bookyourseat.service;

import com.itsmartsystems.bookyourseat.Role;
import com.itsmartsystems.bookyourseat.model.Department;
import com.itsmartsystems.bookyourseat.model.PostgresUser;
import com.itsmartsystems.bookyourseat.model.User;
import com.itsmartsystems.bookyourseat.repository.DepartmentRepository;
import com.itsmartsystems.bookyourseat.repository.PostgresUserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserSyncService {
    private final PostgresUserRepository postgresUserRepository;
    private final DepartmentRepository departmentRepository;

    public UserSyncService(PostgresUserRepository postgresUserRepository, DepartmentRepository departmentRepository) {
        this.postgresUserRepository = postgresUserRepository;
        this.departmentRepository = departmentRepository;
    }

    public PostgresUser syncUser(User mongoUser) {
        PostgresUser postgresUser = postgresUserRepository.findByMongoUserId(mongoUser.getId()).orElseGet(PostgresUser::new);

        postgresUser.setMongoUserId(mongoUser.getId());
        postgresUser.setName(mongoUser.getName());
        postgresUser.setEmail(mongoUser.getEmail());
        postgresUser.setRole(mongoUser.getRole().name());
        postgresUser.setDepartmentId(resolveDepartmentId(mongoUser.getRole()));

        return postgresUserRepository.save(postgresUser);
    }

    public void syncUsers(Iterable<User> mongoUsers) {
        for (User mongoUser : mongoUsers) {
            syncUser(mongoUser);
        }
    }

    private Integer resolveDepartmentId(Role role) {
        String departmentName = departmentNameForRole(role);
        Department department = departmentRepository.findByNameIgnoreCase(departmentName)
                .orElseGet(() -> departmentRepository.save(new Department(departmentName)));

        return Math.toIntExact(department.getId());
    }

    private String departmentNameForRole(Role role) {
        if (role == null) {
            return "Development";
        }

        return switch (role) {
            case CEO, MANAGER -> "Management";
            case PM -> "Project Management";
            case HR -> "Human Resources";
            case DEV, QA, DESIGNER, DEVOPS, INTERN -> "Development";
        };
    }
}