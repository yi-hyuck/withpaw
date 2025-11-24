package com.mysite.test.schedule;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class FcmTestController {

  private final FcmService fcmService;

  @PostMapping("/push")
  public ResponseEntity<String> push(@RequestParam String token,
                                     @RequestParam String title,
                                     @RequestParam String body) {
    fcmService.sendToToken(token, title, body);
    return ResponseEntity.ok("ok");
  }
}
