package com.mysite.test.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceTokenService {
    private final DeviceTokenRepository repo;

    public void register(Long memberId, String token, String platform) {
        if (memberId == null || token == null || token.isBlank()) {
            throw new IllegalArgumentException("memberId와 token은 필수입니다.");
        }
    	
        var existsOpt = repo.findByToken(token);
        if (existsOpt.isPresent()) {
            var exists = existsOpt.get();
            exists.setMemberId(memberId);
            exists.setPlatform(platform);
            if (exists.getEnabled() == null) {
                exists.setEnabled(true);
            }
            exists.setUpdatedAt(LocalDateTime.now());
            return;
        }
        repo.save(DeviceToken.builder()
                .memberId(memberId)
                .token(token)
                .platform(platform)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
    }

    public void unregister(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("token은 필수입니다.");
        }
        repo.deleteByToken(token);
    }
    
    public void setEnabled(String token, boolean enabled) {
        var dt = repo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("해당 토큰이 없습니다."));
        dt.setEnabled(enabled);
        dt.setUpdatedAt(LocalDateTime.now());
    }

}
