package com.ds1.repository;

import com.ds1.entity.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {

    /**
     * Search logs by keyword (username or description) with optional filters
     */
    @Query("SELECT l FROM OperationLog l WHERE " +
           "(:keyword IS NULL OR l.username LIKE %:keyword% OR l.description LIKE %:keyword%) " +
           "AND (:action IS NULL OR l.action = :action) " +
           "AND (:module IS NULL OR l.module = :module) " +
           "AND (:status IS NULL OR l.status = :status) " +
           "ORDER BY l.createdAt DESC")
    Page<OperationLog> searchLogs(@Param("keyword") String keyword,
                                  @Param("action") String action,
                                  @Param("module") String module,
                                  @Param("status") String status,
                                  Pageable pageable);

    /**
     * Count logs created today
     */
    long countByCreatedAtAfter(LocalDateTime dateTime);

    /**
     * Count today's logs by status
     */
    long countByStatusAndCreatedAtAfter(String status, LocalDateTime dateTime);
}
