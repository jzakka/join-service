package com.example.joinservice.service;

import com.example.joinservice.dto.JoinDto;
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

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class JoinServiceImpl implements JoinService{
    private final JoinRepository joinRepository;
    private final ModelMapper mapper;
    private final Environment env;
    private final Validator validator;

    @Override
    public JoinDto joinGather(JoinDto joinGatherDto) {
        JoinEntity member = mapper.map(joinGatherDto, JoinEntity.class);
        member.getSelectDateTimes().forEach(dateTime -> dateTime.setJoin(member));
        validator.validate(joinGatherDto);

        JoinEntity savedResult = joinRepository.save(member);

        return mapper.map(savedResult, JoinDto.class);
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
