package com.example.paymentreceiver.service;

import com.example.paymentreceiver.models.Payment;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
@Slf4j
public class AuditService {

    private String auditFilePath;
    private ObjectMapper objectMapper;

    @Autowired
    public AuditService(@Value("${audit.file.name}") String auditFilePath, ObjectMapper objectMapper) {
        this.auditFilePath = auditFilePath;
        this.objectMapper = objectMapper;
    }

    public void saveToAuditLog(Payment payment) {
        try {
            StandardOpenOption operation = StandardOpenOption.APPEND;
            if (!Paths.get(auditFilePath).toFile().exists()) {
                operation = StandardOpenOption.CREATE;
            }
            final String jsonStr = objectMapper.writeValueAsString(payment) + "\n";
            Files.write(Paths.get(auditFilePath), jsonStr.getBytes(), operation);
        } catch (IOException e) {
            log.error("Error while saving audit", e);
        }
    }

    public String getAuditFilePath() {
        return auditFilePath;
    }

    public void setAuditFilePath(String auditFilePath) {
        this.auditFilePath = auditFilePath;
    }
}
