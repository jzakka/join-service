package com.example.joinservice.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestCancel {
    @NotNull
    private String gatherId;
    @NotNull
    private String memberId;
}
