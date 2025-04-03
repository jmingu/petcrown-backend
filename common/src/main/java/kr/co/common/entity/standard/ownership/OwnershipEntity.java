package kr.co.common.entity.standard.ownership;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.co.common.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ownership")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OwnershipEntity extends BaseEntity {
    @Id
    private Integer ownershipId;

    private String ownershipName;

}
