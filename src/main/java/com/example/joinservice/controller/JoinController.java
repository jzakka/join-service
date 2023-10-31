package com.example.joinservice.controller;

import com.example.joinservice.dto.JoinDto;
import com.example.joinservice.service.JoinService;
import com.example.joinservice.utils.JwtUtils;
import com.example.joinservice.vo.RequestCancel;
import com.example.joinservice.vo.RequestJoin;
import com.example.joinservice.vo.ResponseJoin;
import com.example.joinservice.vo.TokenJoinAuthority;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class JoinController {
    private final JoinService joinService;
    private final ModelMapper mapper;
    private final JwtUtils jwtUtils;

    @PostMapping("/joins")
    public ResponseEntity<ResponseJoin> joinGather(String memberId,
                                                   @RequestBody RequestJoin join,
                                                   HttpServletResponse response) {
        JoinDto joinDto = mapper.map(join, JoinDto.class);
        joinDto.setMemberId(memberId);

        JoinDto joinResultDto = joinService.joinGather(joinDto);

        ResponseJoin body = mapper.map(joinResultDto, ResponseJoin.class);

        regenAuthorizationToken(memberId, response);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{gatherId}/joins")
    public ResponseEntity<List<ResponseJoin>> getJoins(@PathVariable String gatherId) {
        List<ResponseJoin> body = joinService.getJoins(gatherId);

        return ResponseEntity.ok().body(body);
    }

    @GetMapping("/joins/{memberId}")
    public ResponseEntity<List<TokenJoinAuthority>> getJoinedGathers(@PathVariable String memberId,
                                                               HttpServletRequest request) {
        String jwtToken = request.getHeader("Authorization").replace("Bearer", "");
        String realMemberId = jwtUtils.getMemberId(jwtToken);

        List<TokenJoinAuthority> body = joinService.getGathers(realMemberId);

        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/joins")
    public ResponseEntity cancelGather(String memberId,
                                       @RequestBody RequestCancel cancel,
                                       HttpServletResponse response) {
        cancel.setMemberId(memberId);
        joinService.cancelGather(cancel.getGatherId(), cancel.getMemberId());

        regenAuthorizationToken(memberId, response);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/joins")
    public ResponseEntity<ResponseJoin> changeSelectDateTimes(String memberId, @RequestBody RequestJoin join) {
        JoinDto joinDto = mapper.map(join, JoinDto.class);
        joinDto.setMemberId(memberId);

        JoinDto result = joinService.changeSelectDateTimes(joinDto);

        ResponseJoin body = mapper.map(result, ResponseJoin.class);

        return ResponseEntity.ok().body(body);
    }

    private void regenAuthorizationToken(String memberId, HttpServletResponse response) {
        List<TokenJoinAuthority> tokenJoinAuthorities = joinService.getGathers(memberId).stream()
                .map(gatherDto -> new TokenJoinAuthority(gatherDto.getGatherId(), gatherDto.getRule()))
                .toList();
        String regenerateToken = jwtUtils.regenerateToken(memberId, tokenJoinAuthorities);
        response.setHeader("token", regenerateToken);
    }
}
