package com.example.joinservice.dto;

import com.example.joinservice.enums.Rule;
import lombok.Data;

import java.util.*;

@Data
public class JoinDto {
    private String gatherId;
    private String userId;
    private Rule rule;
    private List<SelectDateTimeDto> selectDateTimes = new ArrayList<>();
}
