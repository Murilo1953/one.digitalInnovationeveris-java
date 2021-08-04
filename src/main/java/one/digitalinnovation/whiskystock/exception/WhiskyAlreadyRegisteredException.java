package one.digitalinnovation.whiskystock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WhiskyAlreadyRegisteredException extends Exception{

    public WhiskyAlreadyRegisteredException (String whiskyName) {
        super(String.format("Whisky with name %s already registered in the system.", whiskyName));
    }
}
