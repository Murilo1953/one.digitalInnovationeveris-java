package one.digitalinnovation.whiskystock.builder;

import lombok.Builder;
import one.digitalinnovation.whiskystock.dto.WhiskyDTO;
import one.digitalinnovation.whiskystock.enums.WhiskyType;

@Builder
public class WhiskyDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "Old Parr";

    @Builder.Default
    private String brand = "Scotland";

    @Builder.Default
    private int max = 50;

    @Builder.Default
    private int quantity = 10;

    @Builder.Default
    private WhiskyType type = WhiskyType.OLDPARR;

    public WhiskyDTO toWhiskyDTO () {
        return new WhiskyDTO (id,
                name,
                brand,
                max,
                quantity,
                type);
    }
}
