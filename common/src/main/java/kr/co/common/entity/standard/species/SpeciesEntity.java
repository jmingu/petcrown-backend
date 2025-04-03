package kr.co.common.entity.standard.species;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.co.common.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "species")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SpeciesEntity extends BaseEntity {
    @Id
    private Integer speciesId;

    private String name;

}
