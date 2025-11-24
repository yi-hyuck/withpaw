package com.mysite.test.schedule;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "device_token", indexes = {
        @Index(name = "idx_device_token_member", columnList = "memberId")
})
public class DeviceToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false)
    private Long memberId;          // 회원 ID
    @Column(length = 2048, unique = true, nullable = false)
    private String token;           // FCM registration token
    @Column(length = 16)
    private String platform;        // ANDROID / IOS / WEB 등 (선택)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Column(nullable = false)
    private Boolean enabled = true;
}