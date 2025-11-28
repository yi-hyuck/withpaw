package com.mysite.test.place;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class KakaoPushService {

	@Value("${kakao.push.admin-key}")
	private String adminKey;

	@Value("${kakao.push.base-url}")
	private String baseUrl;

	private final RestTemplate restTemplate = new RestTemplate();

	// 단일 사용자에게 푸시 전송
	public void sendPush(String uuid, String title, String message) {
		String url = baseUrl + "/send";

		Map<String, Object> body = new HashMap<>();
		body.put("uuids", List.of(uuid));

		Map<String, Object> pushMessage = new HashMap<>();
		pushMessage.put("forApns",
				Map.of("badge", 1, "sound", "default", "alert", Map.of("title", title, "body", message)));
		pushMessage.put("forFcm", Map.of("notification", Map.of("title", title, "body", message)));
		body.put("push_message", pushMessage);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "KakaoAK " + adminKey);

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

		if (!response.getStatusCode().is2xxSuccessful()) {
			throw new RuntimeException("카카오 푸시 발송 실패: " + response.getBody());
		}
	}
}
//package com.spring.service;
//
//import org.springframework.stereotype.Service;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Service
//public class KakaoPushService {
//
//    public void sendPush(String uuid, String title, String message) {
//        // 실제 카카오 API 연동 전 테스트용
//        log.info("[테스트용 푸시] uuid={}, title={}, message={}", uuid, title, message);
//    }
//}

