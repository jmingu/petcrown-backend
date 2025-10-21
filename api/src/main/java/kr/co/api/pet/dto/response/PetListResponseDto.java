package kr.co.api.pet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PetListResponseDto {
    
    private List<PetInfoResponseDto> pets;
    private int totalCount;
}