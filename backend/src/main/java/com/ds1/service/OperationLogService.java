package com.ds1.service;

import com.ds1.entity.OperationLog;
import com.ds1.repository.OperationLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OperationLogService {

    private final OperationLogRepository logRepository;

    public OperationLogService(OperationLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    /**
     * Write an operation log entry
     */
    public OperationLog log(String username, String action, String module,
                            String description, String ip, String status) {
        OperationLog opLog = new OperationLog(username, action, module, description, ip, status);
        return logRepository.save(opLog);
    }

    /**
     * Query logs with pagination and filters
     */
    public Map<String, Object> query(int page, int size, String keyword,
                                      String action, String module) {
        Pageable pageable = PageRequest.of(page - 1, size);

        String kw = (keyword != null && !keyword.isEmpty()) ? keyword : null;
        String act = (action != null && !action.isEmpty()) ? action : null;
        String mod = (module != null && !module.isEmpty()) ? module : null;

        Page<OperationLog> pageResult = logRepository.searchLogs(kw, act, mod, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageResult.getContent());
        result.put("total", pageResult.getTotalElements());
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", pageResult.getTotalPages());
        return result;
    }

    /**
     * Get log statistics for today
     */
    public Map<String, Object> getStats() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        long todayTotal = logRepository.countByCreatedAtAfter(startOfToday);
        long todaySuccess = logRepository.countByStatusAndCreatedAtAfter("SUCCESS", startOfToday);
        long todayFail = logRepository.countByStatusAndCreatedAtAfter("FAIL", startOfToday);

        // Distinct action types today
        List<String> actionTypes = Arrays.asList("LOGIN", "LOGOUT", "REGISTER",
                "QUERY", "UPDATE", "DELETE", "CREATE");

        Map<String, Object> stats = new HashMap<>();
        stats.put("todayTotal", todayTotal);
        stats.put("todaySuccess", todaySuccess);
        stats.put("todayFail", todayFail);
        return stats;
    }
}
