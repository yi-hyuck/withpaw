package com.mysite.test.schedule;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FcmConfig {

	@Value("${fcm.credentials.location:classpath:serviceAccountKey.json}")
    private Resource credentials;
	
    @PostConstruct
    public void init() {
        try (InputStream in = credentials.getInputStream()) {
            GoogleCredentials gc = GoogleCredentials.fromStream(in);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(gc)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("[FCM] FirebaseApp initialized");
            } else {
                log.info("[FCM] FirebaseApp already initialized");
            }
        } catch (Exception e) {
            throw new IllegalStateException("FCM init failed: " + e.getMessage(), e);
        }
    }

}
