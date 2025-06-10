package kr.co.common.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
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
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createDate; // 생성 일자
    private Long createUserId;  // 생성 ID
    @LastModifiedDate
    private LocalDateTime updatedDate; // 업데이트 일자
    private Long updateUserId;  // 수정ID
    private LocalDateTime deleteDate; // 삭제 일자
    private String deleteYn; // 삭제여부

    public BaseEntity(Long createUserId, Long updateUserId) {
        this.createUserId = createUserId;
        this.updateUserId = updateUserId;
    }

    public BaseEntity(Long createUserId, Long updateUserId, String deleteYn) {
        this.createUserId = createUserId;
        this.updateUserId = updateUserId;
        this.deleteYn = deleteYn;
    }

    public BaseEntity(LocalDateTime updatedDate, Long updateUserId) {
        this.updatedDate = updatedDate;
        this.updateUserId = updateUserId;
    }
}
