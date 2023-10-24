package com.example.joinservice.service;

import com.example.joinservice.dto.JoinDto;
import com.example.joinservice.dto.SelectDateTimeDto;
import com.example.joinservice.enums.Rule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class JoinServiceImplTest {
    @Autowired
    JoinService joinService;


    @Test
    @DisplayName("모임 참여 취소 테스트 (cascade 잘 작동하는지 확인)")
    void cancel() {
        /**
         * 2077년 10월 3일 4시 5분 ~ 2077년 10월 3일 5시 40분
         * 2077년 10월 5일 5시 7분 ~ 2077년 10월 5일 6시 52분
         *
         * test유저가 위의 시간에 모임이 가능하다는 참여 의사를 요청
         */
        JoinDto joinDto = dummyJoinDto("test-gather-id", "test-user-id", Rule.MEMBER,
                LocalDateTime.of(2077, 10, 3, 4, 5),
                LocalDateTime.of(2077, 10, 3, 5, 40),
                LocalDateTime.of(2077, 10, 5, 5, 7),
                LocalDateTime.of(2077, 10, 5, 6, 52));


        JoinDto joinedDto = joinService.joinGather(joinDto);

        joinService.cancelGather(joinedDto.getGatherId(), joinedDto.getUserId());
    }

    private JoinDto dummyJoinDto(String gatherId,
                                 String userId, Rule rule, LocalDateTime ... selectDateTimes) {
        JoinDto joinDto = new JoinDto();
        joinDto.setGatherId(gatherId);
        joinDto.setUserId(userId);
        joinDto.setRule(rule);

        for (int i = 0; i < selectDateTimes.length; i+=2) {
            SelectDateTimeDto selectDateTimeDto = new SelectDateTimeDto();
            selectDateTimeDto.setStartDateTime(selectDateTimes[i]);
            selectDateTimeDto.setEndDateTime(selectDateTimes[i + 1]);

            joinDto.getSelectDateTimes().add(selectDateTimeDto);
        }

        return joinDto;
    }
}