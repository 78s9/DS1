package com.ds1.controller;

import com.ds1.dto.ApiResponse;
import com.ds1.service.OperationLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/logs")
public class OperationLogController {

    private final OperationLogService logService;

    public OperationLogController(OperationLogService logService) {
        this.logService = logService;
    }

    /**
     * GET /api/logs?page=1&size=20&keyword=&action=&module=
     * Paginated log query with filters
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String module) {

        Map<String, Object> result = logService.query(page, size, keyword, action, module);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * GET /api/logs/stats — Today's log statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        Map<String, Object> stats = logService.getStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
