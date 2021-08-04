package one.digitalinnovation.whiskystock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WhiskyStockExceededException extends Exception {

    public WhiskyStockExceededException (Long id, int quantityToIncrement) {
        super(String.format("Whisky with %s ID to increment informed exceeds the max stock capacity: %s", id, quantityToIncrement));
    }
}
