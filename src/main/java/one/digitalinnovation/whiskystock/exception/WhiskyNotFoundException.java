package one.digitalinnovation.whiskystock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class WhiskyNotFoundException extends Exception {

    public WhiskyNotFoundException (String whiskyName) {
        super(String.format("Whisky with name %s not found in the system.", whiskyName));
    }

    public WhiskyNotFoundException (Long id) {
        super(String.format("Whisky with id %s not found in the system.", id));
    }
}
