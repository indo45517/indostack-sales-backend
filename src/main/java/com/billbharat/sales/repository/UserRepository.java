package com.billbharat.sales.repository;

import com.billbharat.sales.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmployeeId(String employeeId);
    List<User> findByRole(User.Role role);
    List<User> findByTeamLeadId(UUID teamLeadId);
    List<User> findByTerritoryAndRole(String territory, User.Role role);
    long countByRole(User.Role role);
    long countByRoleAndIsActive(User.Role role, boolean isActive);
}
