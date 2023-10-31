package com.example.joinservice.vo;

import com.example.joinservice.enums.Rule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenJoinAuthority {
    private String gatherId;
    private Rule rule;
}
