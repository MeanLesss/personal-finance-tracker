package com.example.common.mappers.responses;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditableView {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}