package com.example.joinservice.service;

import com.example.joinservice.dto.JoinDto;

public interface Validator {
    void validate(JoinDto joinDto);
}
