package kr.co.api.domain.model.breed;

import kr.co.api.domain.model.standard.species.Species;
import kr.co.common.entity.standard.species.SpeciesEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Breed {
    private Integer breedId;

    private Species species;

    private String name;

    public Breed(Integer breedId) {
        this.breedId = breedId;
    }
}
