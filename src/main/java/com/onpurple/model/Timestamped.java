package com.onpurple.model;


import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter // Get메소드 일괄 생성
@MappedSuperclass // 상속했을 때, 컬럼으로 인식하게 합니다.
public class Timestamped {

    @CreatedDate // 생성일자임을 나타냅니다.
    private String createdAt;

    @LastModifiedDate // 마지막 수정일자임을 나타냅니다.
    private String modifiedAt;

    @PrePersist
    public void onPrePersist(){
        this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.modifiedAt = this.createdAt;
    }

    @PreUpdate
    public void onPreUpdate(){
        this.modifiedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}