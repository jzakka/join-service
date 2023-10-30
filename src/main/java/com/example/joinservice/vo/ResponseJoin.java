package com.example.joinservice.vo;

import com.example.joinservice.enums.Rule;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ResponseJoin {
    private String gatherId;
    private String email;
    private String memberId;
    private Rule rule;
    private List<ResponseDateTime> selectDateTimes = new ArrayList<>();

    @Data
    private static class ResponseDateTime {
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
    }
}
