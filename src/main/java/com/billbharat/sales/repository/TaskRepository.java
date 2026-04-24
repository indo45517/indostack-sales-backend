package com.billbharat.sales.repository;

import com.billbharat.sales.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    Page<Task> findByAssignedToOrderByCreatedAtDesc(UUID assignedTo, Pageable pageable);
    Page<Task> findByAssignedByOrderByCreatedAtDesc(UUID assignedBy, Pageable pageable);
    long countByAssignedToAndStatus(UUID assignedTo, Task.Status status);
}
