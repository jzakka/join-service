package com.example.joinservice.vo;

import com.example.joinservice.enums.GatherState;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class ResponseGather {
    private String name;
    private String userId;
    private String description;
    LocalDate startDate;
    LocalDate endDate;
    LocalTime startTime;
    LocalTime endTime;
    LocalTime duration;
    LocalDateTime deadLine;
    private GatherState state;
}
