package kr.co.api.pet.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Ownership{
    private Integer ownershipId;

    private String ownershipName;

    public Ownership(Integer ownershipId) {
        this.ownershipId = ownershipId;
    }
}
