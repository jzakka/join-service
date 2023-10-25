package com.example.joinservice.service;

import com.example.joinservice.client.GatherServiceClient;
import com.example.joinservice.dto.JoinDto;
import com.example.joinservice.dto.SelectDateTimeDto;
import com.example.joinservice.enums.GatherState;
import com.example.joinservice.enums.Rule;
import com.example.joinservice.vo.ResponseGather;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
class JoinServiceImplTest {
    @Autowired
    JoinService joinService;

    @Autowired
    Environment env;

    @MockBean
    GatherServiceClient gatherServiceClient;

    @BeforeEach
    void mockSetting() {
        /**
         * 모임 참가 가능 날짜 2077/10/3 ~ 2077/10/10
         * 참여 가능 시간     03:30 ~ 15:30
         * 모임 진행 기간     01:30
         * 모임 참여 마감     2077/10/2 00:00
         */
        when(gatherServiceClient.getGather("test-gather-id")).thenReturn(
                new ResponseGather(
                        "test-gather",
                        "test-leader-id",
                        "test description",
                        LocalDate.of(2077, 10, 3), // 시작 날짜
                        LocalDate.of(2077, 10, 10), // 끝 날짜
                        LocalTime.of(3, 30), // 시작 시간
                        LocalTime.of(15, 30), // 끝 시간
                        LocalTime.of(1, 30), // 모임 진행 시간
                        LocalDateTime.of(2077, 10, 2, 0, 0), // 마감일
                        GatherState.OPEN
                )
        );

        when(gatherServiceClient.getGather("closed-gather-id")).thenReturn(
                new ResponseGather(
                        "test-gather",
                        "test-leader-id",
                        "test description",
                        LocalDate.of(2077, 10, 3), // 시작 날짜
                        LocalDate.of(2077, 10, 10), // 끝 날짜
                        LocalTime.of(3, 30), // 시작 시간
                        LocalTime.of(15, 30), // 끝 시간
                        LocalTime.of(1, 30), // 모임 진행 시간
                        LocalDateTime.of(2077, 10, 2, 0, 0), // 마감일
                        GatherState.CLOSED
                )
        );
    }

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

    @Test
    @DisplayName("방 입장 테스트 -> 성공")
    void joinGatherTestSuccess() {
        /**
         * 2077년 10월 3일 4시 5분 ~ 2077년 10월 3일 5시 40분
         * 2077년 10월 5일 5시 7분 ~ 2077년 10월 5일 6시 52분
         *
         * test유저가 위의 시간에 모임이 가능하다는 참여 의사를 요청
         */
        JoinDto joinDto = dummyJoinDto(
                "test-gather-id",
                "test-user-id",
                Rule.MEMBER,
                LocalDateTime.of(2077, 10, 3, 4, 5),
                LocalDateTime.of(2077, 10, 3, 5, 40),
                LocalDateTime.of(2077, 10, 5, 5, 7),
                LocalDateTime.of(2077, 10, 5, 6, 52)
        );

        JoinDto joinedResult = joinService.joinGather(joinDto);

        assertThat(joinedResult.getUserId()).isEqualTo("test-user-id");
        assertThat(joinedResult.getRule()).isEqualTo(Rule.MEMBER);
        assertThat(joinedResult.getSelectDateTimes().size()).isEqualTo(2); // 선택한 시간대가 2개임
    }

    @Test
    @DisplayName("너무 이른 시간 선택 입장 테스트")
    void joinGatherInvalidTest1() {
        /**
         * 2077년 10월 3일 4시 5분 ~ 2077년 10월 3일 5시 40분
         * 2077년 10월 5일 2시 7분 ~ 2077년 10월 5일 3시 52분
         *
         * test유저가 위의 시간에 모임이 가능하다는 참여 의사를 요청
         * 두 번째 선택 시간이 걸려야함
         */
        JoinDto joinDto = dummyJoinDto(
                "test-gather-id",
                "test-user-id",
                Rule.MEMBER,
                LocalDateTime.of(2077, 10, 3, 4, 5),
                LocalDateTime.of(2077, 10, 3, 5, 40),
                LocalDateTime.of(2077, 10, 5, 2, 7),
                LocalDateTime.of(2077, 10, 5, 3, 52)
        );

        assertThatThrownBy(() -> joinService.joinGather(joinDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining(env.getProperty("select-time.validation.select-invalid-msg"));
    }

    @Test
    @DisplayName("너무 늦은 시간 입장 테스트")
    void joinGatherInvalidTest2() {
        /**
         * 2077년 10월 3일 4시 5분 ~ 2077년 10월 3일 5시 40분
         * 2077년 10월 11일 4시 7분 ~ 2077년 10월 5일 5시 52분
         *
         * test유저가 위의 시간에 모임이 가능하다는 참여 의사를 요청
         * 두 번째 선택 시간이 걸려야함
         */
        JoinDto joinDto = dummyJoinDto(
                "test-gather-id",
                "test-user-id",
                Rule.MEMBER,
                LocalDateTime.of(2077, 10, 3, 4, 5),
                LocalDateTime.of(2077, 10, 3, 5, 40),
                LocalDateTime.of(2077, 10, 5, 4, 7),
                LocalDateTime.of(2077, 11, 6, 5, 52)
        );

        assertThatThrownBy(() -> joinService.joinGather(joinDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining(env.getProperty("select-time.validation.select-invalid-msg"));
    }

    @Test
    @DisplayName("마감 지나서 입장 테스트")
    void joinGatherAfterDeadLineTest() {
        /**
         * 2077년 10월 3일 4시 5분 ~ 2077년 10월 3일 5시 40분
         * 2077년 10월 5일 5시 7분 ~ 2077년 10월 5일 6시 52분
         *
         * test유저가 위의 시간에 모임이 가능하다는 참여 의사를 요청
         */
        JoinDto joinDto = dummyJoinDto(
                "closed-gather-id",
                "test-user-id",
                Rule.MEMBER,
                LocalDateTime.of(2077, 10, 3, 4, 5),
                LocalDateTime.of(2077, 10, 3, 5, 40),
                LocalDateTime.of(2077, 10, 5, 5, 7),
                LocalDateTime.of(2077, 10, 5, 6, 52)
        );

        assertThatThrownBy(() -> joinService.joinGather(joinDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining(env.getProperty("select-time.validation.deadline-msg"));
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