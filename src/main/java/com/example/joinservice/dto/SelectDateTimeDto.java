package com.example.joinservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SelectDateTimeDto {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
