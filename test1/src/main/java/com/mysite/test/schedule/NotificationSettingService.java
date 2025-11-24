package com.mysite.test.schedule;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationSettingService {

    private final NotificationSettingRepository repo;

    @Transactional(readOnly = true)
    public boolean isPushAllowed(Long memberId) {
        return repo.findById(memberId)
                   .map(NotificationSetting::getPushAllowed)
                   .orElse(true); // 없으면 기본 허용
    }

    @Transactional
    public void setPushAllowed(Long memberId, boolean allowed) {
        NotificationSetting setting = repo.findById(memberId)
                .orElseGet(() -> {
                    NotificationSetting s = new NotificationSetting();
                    s.setMemberId(memberId);
                    return s;
                });
        setting.setPushAllowed(allowed);
        repo.save(setting);
    }
}