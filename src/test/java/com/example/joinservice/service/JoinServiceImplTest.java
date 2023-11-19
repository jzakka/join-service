package com.example.joinservice.service;

import com.example.joinservice.client.GatherServiceClient;
import com.example.joinservice.dto.JoinDto;
import com.example.joinservice.dto.SelectDateTimeDto;
import com.example.joinservice.enums.GatherState;
import com.example.joinservice.enums.Rule;
import com.example.joinservice.vo.ResponseGather;
import com.example.joinservice.vo.ResponseJoin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
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

        /**
         * 새벽에 진행되는 모임
         * 모임 참가 가능 날짜 2077/10/3 ~ 2077/10/5
         * 참여 가능 시간     23:00 ~ 02:00
         * 모임 진행 기간     01:30
         * 모임 참여 마감     2077/10/2 00:00
         */
        when(gatherServiceClient.getGather("midnight-gather-id")).thenReturn(
                new ResponseGather(
                        "test-gather",
                        "test-leader-id",
                        "test description",
                        LocalDate.of(2077, 10, 3), // 시작 날짜
                        LocalDate.of(2077, 10, 5), // 끝 날짜
                        LocalTime.of(23, 00), // 시작 시간
                        LocalTime.of(02, 00), // 끝 시간
                        LocalTime.of(1, 30), // 모임 진행 시간
                        LocalDateTime.of(2077, 10, 2, 0, 0), // 마감일
                        GatherState.OPEN
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

        joinService.cancelGather(joinedDto.getGatherId(), joinedDto.getMemberId());
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

        assertThat(joinedResult.getMemberId()).isEqualTo("test-user-id");
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
         * 두 번째 선택 시간이 제한 조건에 위배되므로 걸려야함
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
         * 두 번째 선택 시간이 제한조건에 위배되므로 걸려야함
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

    @Test
    @DisplayName("모임 참여자 조회")
    void getJoins() {
        JoinDto joinDto1 = dummyJoinDto(
                "test-gather-id",
                "test-user-id1",
                Rule.MEMBER,
                LocalDateTime.of(2077, 10, 3, 4, 5),
                LocalDateTime.of(2077, 10, 3, 5, 40),
                LocalDateTime.of(2077, 10, 5, 5, 7),
                LocalDateTime.of(2077, 10, 5, 6, 52)
        );

        JoinDto joinDto2 = dummyJoinDto(
                "test-gather-id",
                "test-user-id2",
                Rule.MEMBER,
                LocalDateTime.of(2077, 10, 4, 3, 31),
                LocalDateTime.of(2077, 10, 4, 5, 41),
                LocalDateTime.of(2077, 10, 6, 6, 7),
                LocalDateTime.of(2077, 10, 6, 8, 50)
        );

        joinService.joinGather(joinDto1);
        JoinDto joinedResult = joinService.joinGather(joinDto2);

        List<ResponseJoin> joins = joinService.getJoins(joinedResult.getGatherId());

        long selectDatTimesCount = joins.stream().mapToLong(join -> join.getSelectDateTimes().size()).sum();
        assertThat(selectDatTimesCount).isEqualTo(4);
    }

    @Test
    @DisplayName("끝시간이 시작시간보다 앞섬")
    void invalidParticipateTime(){
        JoinDto joinDto1 = dummyJoinDto(
                "test-gather-id",
                "test-user-id1",
                Rule.MEMBER,
                LocalDateTime.of(2077, 10, 3, 6, 0),
                LocalDateTime.of(2077, 10, 3, 4, 0)
        );

        assertThatThrownBy(() -> joinService.joinGather(joinDto1))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining(env.getProperty("select-time.validation.invalid-time-msg"));
    }

    @Test
    @DisplayName("심야 모임 참여, 참여 시간이 자정을 걸침")
    void joinMidnightGather() {
        JoinDto joinDto1 = dummyJoinDto(
                "midnight-gather-id",
                "test-user-id1",
                Rule.MEMBER,
                LocalDateTime.of(2077, 10, 3, 23, 30),
                LocalDateTime.of(2077, 10, 4, 1, 30)
        );

        assertThatCode(() -> joinService.joinGather(joinDto1)).doesNotThrowAnyException();
    }

    private JoinDto dummyJoinDto(String gatherId,
                                 String userId, Rule rule, LocalDateTime ... selectDateTimes) {
        JoinDto joinDto = JoinDto.builder()
                .gatherId(gatherId)
                .gatherName("test-gather")
                .memberId(userId)
                .email("test@test.com")
                .rule(rule)
                .build();

        for (int i = 0; i < selectDateTimes.length; i+=2) {
            SelectDateTimeDto selectDateTimeDto = new SelectDateTimeDto();
            selectDateTimeDto.setStartDateTime(selectDateTimes[i]);
            selectDateTimeDto.setEndDateTime(selectDateTimes[i + 1]);

            joinDto.getSelectDateTimes().add(selectDateTimeDto);
        }

        return joinDto;
    }
}