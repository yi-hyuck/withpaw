package com.mysite.test.schedule;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class NotificationSetting {

    @Id
    private Long memberId;

    @Column(nullable = false)
    private Boolean pushAllowed = true;
}
