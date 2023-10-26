package com.example.joinservice.repository;

import com.example.joinservice.entity.JoinEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JoinRepository extends JpaRepository<JoinEntity, Long> {
    Optional<JoinEntity> findByGatherIdAndUserId(String gatherId, String userId);

    List<JoinEntity> findByGatherId(String gatherId);
}
