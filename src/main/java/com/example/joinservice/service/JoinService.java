package com.example.joinservice.service;

import com.example.joinservice.dto.JoinDto;
import com.example.joinservice.vo.ResponseJoin;
import com.example.joinservice.vo.TokenJoinAuthority;

import java.util.List;

public interface JoinService {
    JoinDto joinGather(JoinDto joinDto);

    void cancelGather(String gatherId, String userId);

    /**
     * @param gatherId
     * @return gatherId의 모임에 참여한 멤버들
     */
    List<ResponseJoin> getJoins(String gatherId);

    JoinDto changeSelectDateTimes(JoinDto join);

    /**
     * @param memberId
     * @return memberId의 멤버가 참가한 모임들
     */
    List<TokenJoinAuthority> getGathers(String memberId);
}
