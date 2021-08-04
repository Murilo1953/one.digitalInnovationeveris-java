package one.digitalinnovation.whiskystock.service;

import one.digitalinnovation.whiskystock.builder.WhiskyDTOBuilder;
import one.digitalinnovation.whiskystock.dto.WhiskyDTO;
import one.digitalinnovation.whiskystock.entity.Whisky;
import one.digitalinnovation.whiskystock.exception.WhiskyAlreadyRegisteredException;
import one.digitalinnovation.whiskystock.exception.WhiskyNotFoundException;
import one.digitalinnovation.whiskystock.exception.WhiskyStockExceededException;
import one.digitalinnovation.whiskystock.mapper.WhiskyMapper;
import one.digitalinnovation.whiskystock.repository.WhiskyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WhiskyServiceTest {

    private static final long INVALID_WHISKY_ID = 1L;

    @Mock
    private WhiskyRepository whiskyRepository;

    private WhiskyMapper whiskyMapper = WhiskyMapper.INSTANCE;

    @InjectMocks
    private WhiskyService whiskyService;

    @Test
    void quandoUmWhiskyInformadoEleDeveSerCriado () throws WhiskyAlreadyRegisteredException {
        // given
        WhiskyDTO expectedWhiskyDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();
        Whisky expectedSavedWhisky = whiskyMapper.toModel (expectedWhiskyDTO);

        // when
        when (whiskyRepository.findByName (expectedWhiskyDTO.getName ())).thenReturn (Optional.empty ());
        when (whiskyRepository.save (expectedSavedWhisky)).thenReturn (expectedSavedWhisky);

        //then
        WhiskyDTO createdWhiskyDTO = whiskyService.createWhisky (expectedWhiskyDTO);

        assertThat (createdWhiskyDTO.getId (), is (equalTo (expectedWhiskyDTO.getId ())));
        assertThat (createdWhiskyDTO.getName (), is (equalTo (expectedWhiskyDTO.getName ())));
        assertThat (createdWhiskyDTO.getQuantity (), is (equalTo (expectedWhiskyDTO.getQuantity ())));
    }

    @Test
    void quandoWhiskyJaRegistradoEInformadoUmaExcecaoDeveSerApresentada () {
        // given
        WhiskyDTO expectedWhiskyDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();
        Whisky duplicatedWhisky = whiskyMapper.toModel (expectedWhiskyDTO);

        // when
        when (whiskyRepository.findByName (expectedWhiskyDTO.getName ())).thenReturn (Optional.of (duplicatedWhisky));

        // then
        assertThrows (WhiskyAlreadyRegisteredException.class, () -> whiskyService.createWhisky (expectedWhiskyDTO));
    }

    @Test
    void quandoNomeValidoWhiskyEDadoRetornaOWhisky () throws WhiskyNotFoundException {
        // given
        WhiskyDTO expectedFoundWhiskyDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();
        Whisky expectedFoundWhisky = whiskyMapper.toModel (expectedFoundWhiskyDTO);

        // when
        when (whiskyRepository.findByName (expectedFoundWhisky.getName ())).thenReturn (Optional.of (expectedFoundWhisky));

        // then
        WhiskyDTO foundWhiskyDTO = whiskyService.findByName (expectedFoundWhiskyDTO.getName ());

        assertThat (foundWhiskyDTO, is (equalTo (expectedFoundWhiskyDTO)));
    }

    @Test
    void quandoWhiskySemRegistroEDadoRetornaUmaExcecao () {
        // given
        WhiskyDTO expectedFoundWhiskyDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();

        // when
        when (whiskyRepository.findByName (expectedFoundWhiskyDTO.getName ())).thenReturn (Optional.empty ());

        // then
        assertThrows (WhiskyNotFoundException.class, () -> whiskyService.findByName (expectedFoundWhiskyDTO.getName ()));
    }

    @Test
    void quandoUmaListaWhiskiesEChamadaRetornaLista () {
        // given
        WhiskyDTO expectedFoundWhiskyDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();
        Whisky expectedFoundWhisky = whiskyMapper.toModel (expectedFoundWhiskyDTO);

        //when
        when (whiskyRepository.findAll ()).thenReturn (Collections.singletonList (expectedFoundWhisky));

        //then
        List<WhiskyDTO> foundListBeersDTO = whiskyService.listAll ();

        assertThat (foundListBeersDTO, is (not (empty ())));
        assertThat (foundListBeersDTO.get (0), is (equalTo (expectedFoundWhiskyDTO)));
    }

    @Test
    void quandoUmaListaVaziaEChamadaRetornaListaVaziaWhiskies () {
        //when
        when (whiskyRepository.findAll ()).thenReturn (Collections.EMPTY_LIST);

        //then
        List<WhiskyDTO> foundListBeersDTO = whiskyService.listAll ();

        assertThat (foundListBeersDTO, is (empty ()));
    }

    @Test
    void quandoExclusaoSolicitadaDeIDValidoWhiskyEDeletado () throws WhiskyNotFoundException {
        // given
        WhiskyDTO expectedDeletedWhiskyDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();
        Whisky expectedDeletedWhisky = whiskyMapper.toModel (expectedDeletedWhiskyDTO);

        // when
        when (whiskyRepository.findById (expectedDeletedWhiskyDTO.getId ())).thenReturn (Optional.of (expectedDeletedWhisky));
        doNothing ().when (whiskyRepository).deleteById (expectedDeletedWhiskyDTO.getId ());

        // then
        whiskyService.deleteById (expectedDeletedWhiskyDTO.getId ());

        verify (whiskyRepository, times (1)).findById (expectedDeletedWhiskyDTO.getId ());
        verify (whiskyRepository, times (1)).deleteById (expectedDeletedWhiskyDTO.getId ());
    }

    @Test
    void quandoIncrementoESolitadoEAdicionadoWhiskyNaLista () throws WhiskyNotFoundException, WhiskyStockExceededException {
        //given
        WhiskyDTO expectedWhiskyDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();
        Whisky expectedWhisky = whiskyMapper.toModel (expectedWhiskyDTO);

        //when
        when (whiskyRepository.findById (expectedWhiskyDTO.getId ())).thenReturn (Optional.of (expectedWhisky));
        when (whiskyRepository.save (expectedWhisky)).thenReturn (expectedWhisky);

        int quantityToIncrement = 10;
        int expectedQuantityAfterIncrement = expectedWhiskyDTO.getQuantity () + quantityToIncrement;

        // then
        WhiskyDTO incrementedWhiskyDTO = whiskyService.increment (expectedWhiskyDTO.getId (), quantityToIncrement);

        assertThat (expectedQuantityAfterIncrement, equalTo (incrementedWhiskyDTO.getQuantity ()));
        assertThat (expectedQuantityAfterIncrement, lessThan (expectedWhiskyDTO.getMax ()));
    }

    @Test
    void quandoIncrementoMaiorQueMaximoRetornaUmaExcecao () {
        WhiskyDTO expectedWhiskyDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();
        Whisky expectedWhisky = whiskyMapper.toModel (expectedWhiskyDTO);

        when (whiskyRepository.findById (expectedWhiskyDTO.getId ())).thenReturn (Optional.of (expectedWhisky));

        int quantityToIncrement = 80;
        assertThrows (WhiskyStockExceededException.class, () -> whiskyService.increment (expectedWhiskyDTO.getId (), quantityToIncrement));
    }

    @Test
    void quandoIncrementoAposSomaMaiorMaxRetornaUmaExcecao () {
        WhiskyDTO expectedWhiskyDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();
        Whisky expectedWhisky = whiskyMapper.toModel (expectedWhiskyDTO);

        when (whiskyRepository.findById (expectedWhiskyDTO.getId ())).thenReturn (Optional.of (expectedWhisky));

        int quantityToIncrement = 45;
        assertThrows (WhiskyStockExceededException.class, () -> whiskyService.increment (expectedWhiskyDTO.getId (), quantityToIncrement));
    }

    @Test
    void quandoIncrementoSolicitadoIDInvalidoRetornaUmaExcecao () {
        int quantityToIncrement = 10;

        when (whiskyRepository.findById (INVALID_WHISKY_ID)).thenReturn (Optional.empty ());

        assertThrows (WhiskyNotFoundException.class, () -> whiskyService.increment (INVALID_WHISKY_ID, quantityToIncrement));
    }

    @Test
    void quanoDecrementoSolicitadoDiminuidoWhiskyDoEstoque () throws WhiskyNotFoundException, WhiskyStockExceededException {
        WhiskyDTO expectedWhiskyDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();
        Whisky expectedWhisky = whiskyMapper.toModel (expectedWhiskyDTO);

        when (whiskyRepository.findById (expectedWhiskyDTO.getId ())).thenReturn (Optional.of (expectedWhisky));
        when (whiskyRepository.save (expectedWhisky)).thenReturn (expectedWhisky);
//
        int quantityToDecrement = 5;
        int expectedQuantityAfterDecrement = expectedWhiskyDTO.getQuantity () - quantityToDecrement;
        WhiskyDTO incrementedBeerDTO = whiskyService.decrement (expectedWhiskyDTO.getId (), quantityToDecrement);

        assertThat (expectedQuantityAfterDecrement, equalTo (incrementedBeerDTO.getQuantity ()));
        assertThat (expectedQuantityAfterDecrement, greaterThan (0));
    }

    @Test
    void quandoDecrementoSolicitadoemEstoqueVazioRetornaEstoqueVazio () throws WhiskyNotFoundException, WhiskyStockExceededException {
        WhiskyDTO expectedWhiskyDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();
        Whisky expectedWhisky = whiskyMapper.toModel (expectedWhiskyDTO);

        when (whiskyRepository.findById (expectedWhiskyDTO.getId ())).thenReturn (Optional.of (expectedWhisky));
        when (whiskyRepository.save (expectedWhisky)).thenReturn (expectedWhisky);

        int quantityToDecrement = 10;
        int expectedQuantityAfterDecrement = expectedWhiskyDTO.getQuantity () - quantityToDecrement;
        WhiskyDTO incrementedWhiskyDTO = whiskyService.decrement (expectedWhiskyDTO.getId (), quantityToDecrement);

        assertThat (expectedQuantityAfterDecrement, equalTo (0));
        assertThat (expectedQuantityAfterDecrement, equalTo (incrementedWhiskyDTO.getQuantity ()));
    }

    @Test
    void QuandoDecrementoSolicitadoEmEstoqueVazioUmaExcecaoERetornada () {
        WhiskyDTO expectedWhiskyDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();
        Whisky expectedWhisky = whiskyMapper.toModel (expectedWhiskyDTO);

        when (whiskyRepository.findById (expectedWhiskyDTO.getId ())).thenReturn (Optional.of (expectedWhisky));

        int quantityToDecrement = 80;
        assertThrows (WhiskyStockExceededException.class, () -> whiskyService.decrement (expectedWhiskyDTO.getId (), quantityToDecrement));
    }

    @Test
    void quandoDecrementoSolicitadoParaIDInvalidoRetornaUmaExcecao () {
        int quantityToDecrement = 10;

        when (whiskyRepository.findById (INVALID_WHISKY_ID)).thenReturn (Optional.empty ());

        assertThrows (WhiskyNotFoundException.class, () -> whiskyService.decrement (INVALID_WHISKY_ID, quantityToDecrement));
    }
}
