package com.project.shop.global.common;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass // JPA의 엔티티 클래스가 상속받을 경우 자식 클래스에게 매핑 정보를 전달
// 엔티티를 데이터베이스에 적용하기 전후로 콜백 요청 가능 , 엔티티의 auditing 정보를 주입
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntityTime {

    @CreatedDate // 데이터 생성날짜를 자동으로 주입
    @Column(updatable = false)
    private LocalDateTime cratedAt;

    @LastModifiedDate // 데이터 수정날짜를 자동으로 주입
    private LocalDateTime updatedAt;
}
