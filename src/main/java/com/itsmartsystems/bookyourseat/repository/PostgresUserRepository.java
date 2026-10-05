package com.itsmartsystems.bookyourseat.repository;

import com.itsmartsystems.bookyourseat.model.PostgresUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostgresUserRepository extends JpaRepository<PostgresUser, Integer> {

    Optional<PostgresUser> findByEmail(String email);

    boolean existsByEmail(String email);

    List<PostgresUser> findByRole(String role);

    List<PostgresUser> findByDepartmentId(Integer departmentId);

    Optional<PostgresUser> findByMongoUserId(String id);
}