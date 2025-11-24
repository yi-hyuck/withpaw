package com.mysite.test.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    List<DeviceToken> findAllByMemberId(Long memberId);
    List<DeviceToken> findAllByMemberIdAndEnabledTrue(Long memberId);
    Optional<DeviceToken> findByToken(String token);
    void deleteByToken(String token);
}
