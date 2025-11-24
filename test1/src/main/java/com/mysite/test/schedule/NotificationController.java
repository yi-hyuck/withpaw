package com.mysite.test.schedule;

import com.mysite.test.ApiResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final DeviceTokenService deviceTokenService;

    @PostMapping("/register-token")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody RegisterTokenReq req) {
        deviceTokenService.register(req.getMemberId(), req.getToken(), req.getPlatform());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "토큰 등록 완료", null));
    }

    @DeleteMapping("/unregister-token")
    public ResponseEntity<ApiResponse<Void>> unregister(@RequestParam("token") String token) {
        deviceTokenService.unregister(token);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "토큰 해제 완료", null));
    }

    @Data
    public static class RegisterTokenReq {
        private Long memberId;
        private String token;     // 앱에서 받은 FCM token
        private String platform;  // ANDROID/IOS/WEB 등 (선택)
    }
}