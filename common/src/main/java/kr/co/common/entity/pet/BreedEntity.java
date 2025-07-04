package kr.co.common.entity.pet;

import jakarta.persistence.*;
import kr.co.common.entity.base.BaseEntity;
import kr.co.common.entity.standard.species.SpeciesEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "breed")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BreedEntity extends BaseEntity {
    @Id
    private Integer breedId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id")
    private SpeciesEntity species;

    private String name;

}
