package com.example.joinservice.service;

import com.example.joinservice.client.GatherServiceClient;
import com.example.joinservice.dto.JoinDto;
import com.example.joinservice.dto.SelectDateTimeDto;
import com.example.joinservice.enums.GatherState;
import com.example.joinservice.vo.ResponseGather;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ValidatorImpl implements Validator{
    private final GatherServiceClient gatherServiceClient;
    private final Environment env;

    @Override
    public void validate(JoinDto joinGatherDto) {
        /**
         * 사용자 선택 날짜, 시간은 모임의 시작 날짜, 시간보다 이를 수 없다.
         * 사용자 선택 날짜, 시간은 모임의 끝 날짜, 시간보다 늦을 수 없다.
         * 선택한 시작시간이 끝시간보다 늦을 수 없다.
         * 현재 시간이 모임 마감날짜보다 늦다면 참여가 불가능하다.
         */
        String errorMessage = null;

        ResponseGather gather = gatherServiceClient.getGather(joinGatherDto.getGatherId());

        for (SelectDateTimeDto selectDateTime : joinGatherDto.getSelectDateTimes()) {
            LocalDateTime startDateTime = selectDateTime.getStartDateTime();
            LocalDateTime endDateTime = selectDateTime.getEndDateTime();


            if(endDateTime.isBefore(startDateTime)){
                errorMessage = env.getProperty("select-time.validation.invalid-time-msg");
                break;
            }
            else if (isOutOfTimeRange(gather, startDateTime, endDateTime)) {
                errorMessage = env.getProperty("select-time.validation.select-invalid-msg");
                break;
            } else if (!gather.getState().equals(GatherState.OPEN)) {
                errorMessage = env.getProperty("select-time.validation.deadline-msg");
                break;
            }
        }

        if (errorMessage != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }

    private boolean isOutOfTimeRange(ResponseGather gather, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return startDateTime.toLocalDate().isBefore(gather.getStartDate())
                || startDateTime.toLocalTime().isBefore(gather.getStartTime())
                || endDateTime.toLocalDate().isAfter(gather.getEndDate())
                || endDateTime.toLocalTime().isAfter(gather.getEndTime());
    }
}
