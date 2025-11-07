package com.spring.service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.spring.domain.Place;
import com.spring.dto.PlaceResponseDTO;
import com.spring.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class PlaceService {

	@Value("${kakao.api.key}")
	private String kakaoApiKey;

	private final PlaceRepository placeRepository;

	private final WebClient webClient;

	public List<PlaceResponseDTO> searchPlaces(String query, double lat, double lng, int radius) {
		try {
			// URI 생성
			URI uri = URI.create("https://dapi.kakao.com/v2/local/search/keyword.json" + "?query="
					+ URLEncoder.encode(query, StandardCharsets.UTF_8) + "&x=" + lng + "&y=" + lat + "&radius="
					+ radius);

			log.info("Requesting Kakao API: {}", uri);

			// API 요청
			Map<String, Object> response = webClient.get().uri(uri).header("Authorization", "KakaoAK " + kakaoApiKey)
					.retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
					}).block();

			if (response == null || !response.containsKey("documents")) {
				throw new IllegalStateException("카카오 API 응답이 비어 있거나 형식이 올바르지 않습니다.");
			}

			List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");

			for (Map<String, Object> doc : documents) {
				String name = (String) doc.get("place_name");
				String address = (String) doc.get("address_name");

				// 이미 존재하는지 확인
				Place place = placeRepository.findByNameAndAddress(name, address)
						.orElseGet(() -> Place.builder().name(name).address(address)
								.roadAddress((String) doc.get("road_address_name")).phone((String) doc.get("phone"))
								.category((String) doc.get("category_name"))
								.latitude(Double.parseDouble((String) doc.get("y")))
								.longitude(Double.parseDouble((String) doc.get("x")))
								.placeUrl((String) doc.get("place_url")).source("KAKAO").build());

				// 저장 (신규면 insert, 있으면 update)
				placeRepository.save(place);
			}

			log.info("Kakao API Raw Response: {}", response);

			if (documents == null || documents.isEmpty()) {
				log.warn("검색 결과 없음: {}", query);
				return List.of();
			}

			// DTO 변환
			return documents.stream().map(doc -> {
				PlaceResponseDTO dto = new PlaceResponseDTO();
				dto.setName((String) doc.get("place_name"));
				dto.setAddress((String) doc.get("address_name"));
				dto.setRoadAddress((String) doc.get("road_address_name"));
				dto.setPhone((String) doc.get("phone"));
				dto.setLatitude((String) doc.get("y"));
				dto.setLongitude((String) doc.get("x"));
				dto.setUrl((String) doc.get("place_url"));
				return dto;
			}).toList();

		} catch (WebClientResponseException e) {
		    log.error("카카오 API 호출 실패: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());

		    String errorMessage;
		    if (e.getStatusCode().is4xxClientError()) {
		        if (e.getStatusCode().value() == 401) {
		            errorMessage = "API 키가 유효하지 않습니다. (401 Unauthorized)";
		        } else if (e.getStatusCode().value() == 400) {
		            errorMessage = "요청 파라미터가 잘못되었습니다. (400 Bad Request)";
		        } else {
		            errorMessage = "잘못된 요청입니다: " + e.getStatusCode();
		        }
		    } else if (e.getStatusCode().is5xxServerError()) {
		        errorMessage = "카카오 서버 오류입니다. 잠시 후 다시 시도해주세요.";
		    } else {
		        errorMessage = "예상치 못한 오류가 발생했습니다.";
		    }

		    throw new RuntimeException(errorMessage);

		} catch (IllegalStateException e) {
		    log.warn("응답 데이터 형식 오류: {}", e.getMessage());
		    throw new RuntimeException("카카오 API 응답이 비정상적입니다.");

		} catch (Exception e) {
		    log.error("장소 검색 중 알 수 없는 오류 발생", e);
		    throw new RuntimeException("장소 검색 중 오류가 발생했습니다. 원인: " + e.getMessage());
		}
	}
}
