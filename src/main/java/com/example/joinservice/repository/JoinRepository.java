package com.example.joinservice.repository;

import com.example.joinservice.entity.JoinEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface JoinRepository extends JpaRepository<JoinEntity, Long> {
    Optional<JoinEntity> findByGatherIdAndMemberId(String gatherId, String memberId);

    List<JoinEntity> findByGatherId(String gatherId);

    @Modifying
    @Transactional
    @Query("delete from SelectDateTimeEntity s where s.join = :join")
    void deleteSelectDateTimes(@Param("join") JoinEntity join);

    @Transactional
    default void deleteJoinAndSelectDateTimes(JoinEntity join) {
        deleteSelectDateTimes(join);
        delete(join);
    }
}
