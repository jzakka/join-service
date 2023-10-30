package com.example.joinservice.vo;

import com.example.joinservice.enums.Rule;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class RequestJoin {
    private String gatherId;
    private String email;
    private String memberId;
    private Rule rule;
    private List<RequestDateTime> selectDateTimes = new ArrayList<>();

    @Data
    private static class RequestDateTime {
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
    }
}
