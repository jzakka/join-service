package com.example.joinservice.service;

import com.example.joinservice.dto.JoinDto;

public interface JoinService {
    JoinDto joinGather(JoinDto joinDto);

    void cancelGather(String gatherId, String userId);
}
