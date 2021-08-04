package one.digitalinnovation.whiskystock.service;

import lombok.AllArgsConstructor;
import one.digitalinnovation.whiskystock.dto.WhiskyDTO;
import one.digitalinnovation.whiskystock.entity.Whisky;
import one.digitalinnovation.whiskystock.exception.WhiskyAlreadyRegisteredException;
import one.digitalinnovation.whiskystock.exception.WhiskyNotFoundException;
import one.digitalinnovation.whiskystock.exception.WhiskyStockExceededException;
import one.digitalinnovation.whiskystock.mapper.WhiskyMapper;
import one.digitalinnovation.whiskystock.repository.WhiskyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class WhiskyService {


    private final WhiskyRepository whiskyRepository;
    private final WhiskyMapper whiskyMapper = WhiskyMapper.INSTANCE;

    public WhiskyDTO createWhisky (WhiskyDTO whiskyDTO) throws WhiskyAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered (whiskyDTO.getName ());
        Whisky whisky = whiskyMapper.toModel (whiskyDTO);
        Whisky savedWhisky = whiskyRepository.save (whisky);
        return whiskyMapper.toDTO (savedWhisky);
    }

    public WhiskyDTO findByName (String name) throws WhiskyNotFoundException {
        Whisky foundWhisky = whiskyRepository.findByName (name)
                .orElseThrow (() -> new WhiskyNotFoundException (name));
        return whiskyMapper.toDTO (foundWhisky);
    }

    public List<WhiskyDTO> listAll () {
        return whiskyRepository.findAll ()
                .stream ()
                .map (whiskyMapper::toDTO)
                .collect (Collectors.toList ());
    }

    public void deleteById (Long id) throws WhiskyNotFoundException {
        verifyIfExists (id);
        whiskyRepository.deleteById (id);
    }

    private void verifyIfIsAlreadyRegistered (String name) throws WhiskyAlreadyRegisteredException {
        Optional<Whisky> optSavedWhisky = whiskyRepository.findByName (name);
        if (optSavedWhisky.isPresent ()) {
            throw new WhiskyAlreadyRegisteredException (name);
        }
    }

    private Whisky verifyIfExists (Long id) throws WhiskyNotFoundException {
        return whiskyRepository.findById (id)
                .orElseThrow (() -> new WhiskyNotFoundException (id));
    }

    public WhiskyDTO increment (Long id, int quantityToIncrement) throws WhiskyNotFoundException, WhiskyStockExceededException {
        Whisky whiskyToIncrementStock = verifyIfExists (id);
        int quantityAfterIncrement = quantityToIncrement + whiskyToIncrementStock.getQuantity ();
        if (quantityAfterIncrement <= whiskyToIncrementStock.getMax ()) {
            whiskyToIncrementStock.setQuantity (whiskyToIncrementStock.getQuantity () + quantityToIncrement);
            Whisky incrementedWhiskyStock = whiskyRepository.save (whiskyToIncrementStock);
            return whiskyMapper.toDTO (incrementedWhiskyStock);
        }
        throw new WhiskyStockExceededException (id, quantityToIncrement);
    }
    public WhiskyDTO decrement(Long id, int quantityToDecrement) throws WhiskyNotFoundException, WhiskyStockExceededException {
        Whisky beerToDecrementStock = verifyIfExists(id);
        int beerStockAfterDecremented = beerToDecrementStock.getQuantity() - quantityToDecrement;
        if (beerStockAfterDecremented >= 0) {
            beerToDecrementStock.setQuantity (beerStockAfterDecremented);
            Whisky decrementedBeerStock = whiskyRepository.save (beerToDecrementStock);
            return whiskyMapper.toDTO (decrementedBeerStock);
        }
        throw new WhiskyStockExceededException (id, quantityToDecrement);
    }
}
