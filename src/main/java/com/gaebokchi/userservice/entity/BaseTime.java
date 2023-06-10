package com.gaebokchi.userservice.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTime {

    @Column(name = "created_datetime")
    @CreatedDate
    private LocalDateTime createdDatetime;

    @Column(name = "modified_datetime")
    @LastModifiedDate
    private LocalDateTime modifiedDatetime;

    /* 해당 엔티티를 업데이트 하기 이전에 실행*/

    @PreUpdate
    public void onPreUpdate() {
        this.modifiedDatetime = LocalDateTime.now();
    }
}
