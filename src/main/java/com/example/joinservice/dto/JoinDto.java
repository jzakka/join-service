package com.example.joinservice.dto;

import com.example.joinservice.enums.Rule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinDto {
    private String gatherId;
    private String memberId;
    private String email;
    private Rule rule;
    @Builder.Default
    private List<SelectDateTimeDto> selectDateTimes = new ArrayList<>();
}
