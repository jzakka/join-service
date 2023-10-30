package com.example.joinservice.vo;

import com.example.joinservice.enums.Rule;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class RequestJoin {
    @NotNull
    private String gatherId;
    @NotNull
    private String gatherName;
    @NotNull
    @Email
    private String email;
    @NotNull
    private String memberId;
    @NotNull
    private Rule rule;
    @NotNull
    private List<RequestDateTime> selectDateTimes = new ArrayList<>();

    @Data
    private static class RequestDateTime {
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
    }
}
