package com.example.demo.service;

import com.example.demo.repository.AuditLogRepository;

public class AuditService {
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String username,
                    String action,
                    String target,
                    String status,
                    String ip,
                    String details) {
        var auditLog = com.example.demo.entity.AuditLog.builder()
                .username(username)
                .action(action)
                .target(target)
                .status(status)
                .ipAddress(ip)
                .details(details)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
    }
}
