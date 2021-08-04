package one.digitalinnovation.whiskystock.controller;

import lombok.AllArgsConstructor;
import one.digitalinnovation.whiskystock.dto.WhiskyDTO;
import one.digitalinnovation.whiskystock.dto.WhiskyQuantityDTO;
import one.digitalinnovation.whiskystock.exception.WhiskyAlreadyRegisteredException;
import one.digitalinnovation.whiskystock.exception.WhiskyNotFoundException;
import one.digitalinnovation.whiskystock.exception.WhiskyStockExceededException;
import one.digitalinnovation.whiskystock.service.WhiskyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/whiskies")
@AllArgsConstructor//(onConstructor = @__(@Autowired))
public class WhiskyController implements WhiskyControllerDocs {

    private final WhiskyService whiskyService;
@Autowired
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WhiskyDTO createWhisky(@RequestBody @Valid WhiskyDTO whiskyDTO) throws WhiskyAlreadyRegisteredException {
        return whiskyService.createWhisky(whiskyDTO);
    }
@Autowired
    @GetMapping("/{name}")
    public WhiskyDTO findByName(@PathVariable String name) throws WhiskyNotFoundException {
        return whiskyService.findByName(name);
    }
@Autowired
    @GetMapping
    public List<WhiskyDTO> listWhisky() {
        return whiskyService.listAll();
    }
@Autowired
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws WhiskyNotFoundException {
        whiskyService.deleteById(id);
    }
@Autowired
    @PatchMapping("/{id}/increment")
    public WhiskyDTO increment(@PathVariable Long id, @RequestBody @Valid WhiskyQuantityDTO whiskyQuantityDTO) throws WhiskyNotFoundException, WhiskyStockExceededException {
        return whiskyService.increment(id, whiskyQuantityDTO.getQuantity());
    }
}
