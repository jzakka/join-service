package com.example.joinservice.service;

import com.example.joinservice.client.GatherServiceClient;
import com.example.joinservice.enums.GatherState;
import com.example.joinservice.vo.ResponseGather;
import com.example.joinservice.dto.JoinDto;
import com.example.joinservice.dto.SelectDateTimeDto;
import com.example.joinservice.entity.JoinEntity;
import com.example.joinservice.repository.JoinRepository;
import com.example.joinservice.vo.ResponseJoin;
import com.example.joinservice.vo.TokenJoinAuthority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class JoinServiceImpl implements JoinService{
    private final JoinRepository joinRepository;
    private final ModelMapper mapper;
    private final Environment env;
    private final GatherServiceClient gatherServiceClient;

    @Override
    public JoinDto joinGather(JoinDto joinGatherDto) {
        JoinEntity member = mapper.map(joinGatherDto, JoinEntity.class);
        member.getSelectDateTimes().forEach(dateTime -> dateTime.setJoin(member));
        validate(joinGatherDto);

        JoinEntity savedResult = joinRepository.save(member);

        return mapper.map(savedResult, JoinDto.class);
    }

    private void validate(JoinDto joinGatherDto) {
        /**
         * 사용자 선택 날짜, 시간은 모임의 시작 날짜, 시간보다 이를 수 없다.
         * 사용자 선택 날짜, 시간은 모임의 끝 날짜, 시간보다 늦을 수 없다.
         * 현재 시간이 모임 마감날짜보다 늦다면 참여가 불가능하다.
         */
        String errorMessage = null;

        ResponseGather gather = gatherServiceClient.getGather(joinGatherDto.getGatherId());

        for (SelectDateTimeDto selectDateTime : joinGatherDto.getSelectDateTimes()) {
            LocalDateTime startDateTime = selectDateTime.getStartDateTime();
            LocalDateTime endDateTime = selectDateTime.getEndDateTime();

            if (startDateTime.toLocalDate().isBefore(gather.getStartDate())
                    || startDateTime.toLocalTime().isBefore(gather.getStartTime())
                    || endDateTime.toLocalDate().isAfter(gather.getEndDate())
                    || endDateTime.toLocalTime().isAfter(gather.getEndTime())) {
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

    @Override
    public void cancelGather(String gatherId, String userId) {
        JoinEntity gatherMember = joinRepository
                .findByGatherIdAndMemberId(gatherId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, env.getProperty("gather.member.not-found-msg")));

        joinRepository.deleteJoinAndSelectDateTimes(gatherMember);
    }

    @Override
    public List<ResponseJoin> getJoins(String gatherId) {
        List<JoinEntity> members = joinRepository.findByGatherId(gatherId);

        return members.stream().map(gather -> mapper.map(gather, ResponseJoin.class)).toList();
    }

    @Override
    public JoinDto changeSelectDateTimes(JoinDto join) {
        cancelGather(join.getGatherId(), join.getMemberId());

        joinRepository.flush();

        return joinGather(join);
    }

    @Override
    public List<TokenJoinAuthority> getGathers(String memberId) {
        List<JoinEntity> gathers = joinRepository.findByMemberId(memberId);

        return gathers.stream().map(gather -> new TokenJoinAuthority(gather.getGatherId(), gather.getRule())).toList();
    }
}
