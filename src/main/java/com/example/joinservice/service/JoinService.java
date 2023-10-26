package com.example.joinservice.service;

import com.example.joinservice.dto.JoinDto;
import com.example.joinservice.vo.ResponseJoin;

import java.util.List;

public interface JoinService {
    JoinDto joinGather(JoinDto joinDto);

    void cancelGather(String gatherId, String userId);

    List<ResponseJoin> getJoins(String gatherId);
}
