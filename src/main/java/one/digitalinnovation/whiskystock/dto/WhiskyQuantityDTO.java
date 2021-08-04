package one.digitalinnovation.whiskystock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhiskyQuantityDTO {

    @NotNull
    @Max(100)
    private Integer quantity;
}
