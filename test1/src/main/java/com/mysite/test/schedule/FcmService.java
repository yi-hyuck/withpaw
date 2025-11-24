package com.mysite.test.schedule;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FcmService {

    public void sendToToken(String token, String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 푸시 성공: {}", response);
        } catch (FirebaseMessagingException e) {
            log.warn("FCM 푸시 실패: {}, code={}", e.getMessage(), e.getErrorCode());
            throw new RuntimeException("FCM send failed: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("FCM 푸시 예외", e);
        }
    }
}
