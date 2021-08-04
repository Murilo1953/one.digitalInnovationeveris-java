package one.digitalinnovation.whiskystock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WhiskyType {

    OLDPARR("Old Parr"),
    WHITEHORSE("White Horse"),
    JOHNNYWALKER("Johnny Walker"),
    BALANTINES("Balantines"),
    ROYALSALUTE("Royal Salute"),
    JAMESON("Jameson"),
    GRANTS("Grants");

    private final String description;
}
