package kr.co.common.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class) // @EnableJpaAuditing 필요 (자동으로 시간넣어준다)
@MappedSuperclass
@Getter
@NoArgsConstructor
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createDate; // 생성 일자
    private Long createUserId;  // 생성 ID
    @LastModifiedDate
    private LocalDateTime updatedDate; // 업데이트 일자
    private Long updateUserId;  // 수정ID

    public BaseTimeEntity(Long createUserId, Long updateUserId) {
        this.createUserId = createUserId;
        this.updateUserId = updateUserId;
    }

}
