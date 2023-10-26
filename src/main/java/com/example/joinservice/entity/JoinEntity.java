package com.example.joinservice.entity;

import com.example.joinservice.enums.Rule;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "joins", uniqueConstraints = {
        @UniqueConstraint(
                name = "gatherMember",
                columnNames = {"gatherId", "userId"}
        )
})
@EntityListeners(AuditingEntityListener.class)
public class JoinEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String gatherId;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Rule rule;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "join", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<SelectDateTimeEntity> selectDateTimes = new ArrayList<>();
    @CreatedDate
    private LocalDateTime createdAt;
}
