package com.hit.employee_management_spring.domain.entity;

import com.hit.employee_management_spring.audit.FullAuditing;
import com.hit.employee_management_spring.constant.TypeToken;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tokens_blacklist")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenBlacklist extends FullAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "varchar(512)")
    private String token;

    @Enumerated(EnumType.STRING)
    private TypeToken typeToken;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

}
