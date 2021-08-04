package one.digitalinnovation.whiskystock.controller;

import one.digitalinnovation.whiskystock.builder.WhiskyDTOBuilder;
import one.digitalinnovation.whiskystock.dto.WhiskyDTO;
import one.digitalinnovation.whiskystock.dto.WhiskyQuantityDTO;
import one.digitalinnovation.whiskystock.exception.WhiskyNotFoundException;
import one.digitalinnovation.whiskystock.exception.WhiskyStockExceededException;
import one.digitalinnovation.whiskystock.service.WhiskyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import java.util.Collections;
import static one.digitalinnovation.whiskystock.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class WhiskyControllerTest {

    private static final String WHISKY_API_URL_PATH = "/api/v1/whiskies";
    private static final long VALID_WHISKY_ID = 1L;
    private static final long INVALID_WHISKY_ID = 2l;
    private static final String WHISKY_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String WHISKY_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private WhiskyService whiskyService;

    @InjectMocks
    private WhiskyController whiskyController;

    @BeforeEach
    void setUp () {
        mockMvc = MockMvcBuilders.standaloneSetup (whiskyController)
                .setCustomArgumentResolvers (new PageableHandlerMethodArgumentResolver ())
                .setViewResolvers ((s, locale) -> new MappingJackson2JsonView ())
                .build ();
    }

    @Test
    void criadoUmPOSTParaLancamentoDeUmWhisky () throws Exception {
        // given
        WhiskyDTO whiskyDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();

        // when
        when (whiskyService.createWhisky (whiskyDTO)).thenReturn (whiskyDTO);

        // then
        mockMvc.perform (post (WHISKY_API_URL_PATH)
                .contentType (MediaType.APPLICATION_JSON)
                .content (asJsonString (whiskyDTO)))
                .andExpect (status ().isCreated ())
                .andExpect (jsonPath ("$.name", is (whiskyDTO.getName ())))
                .andExpect (jsonPath ("$.brand", is (whiskyDTO.getBrand ())))
                .andExpect (jsonPath ("$.type", is (whiskyDTO.getType ().toString ())));
    }

    @Test
    void umPOSTLancadoSemCampoObrigatórioEUmErroDeveSerRetornado () throws Exception {
        // given
        WhiskyDTO whiskyDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();
        whiskyDTO.setBrand (null);

        // then
        mockMvc.perform (post (WHISKY_API_URL_PATH)
                .contentType (MediaType.APPLICATION_JSON)
                .content (asJsonString (whiskyDTO)))
                .andExpect (status ().isBadRequest ());
    }

    @Test
    void umGETVálidoEChamadoEStatusOKDeveSerRetornado () throws Exception {
        // given
        WhiskyDTO whiskyDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();

        //when
        when (whiskyService.findByName (whiskyDTO.getName ())).thenReturn (whiskyDTO);

        // then
        mockMvc.perform (MockMvcRequestBuilders.get (WHISKY_API_URL_PATH +
                "/" + whiskyDTO.getName ())
                .contentType (MediaType.APPLICATION_JSON))
                .andExpect (status ().isOk ())
                .andExpect (jsonPath ("$.name", is (whiskyDTO.getName ())))
                .andExpect (jsonPath ("$.brand", is (whiskyDTO.getBrand ())))
                .andExpect (jsonPath ("$.type", is (whiskyDTO.getType ().toString ())));
    }

    @Test
    void quandoGETSemNomeRegistradoEChamadoEStatusNãoEncontradoERetornado () throws Exception {
        // given
        WhiskyDTO whiskyDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();

        //when
        when (whiskyService.findByName (whiskyDTO.getName ())).thenThrow (WhiskyNotFoundException.class);

        // then
        mockMvc.perform (MockMvcRequestBuilders.get (WHISKY_API_URL_PATH + "/" + whiskyDTO.getName ())
                .contentType (MediaType.APPLICATION_JSON))
                .andExpect (status ().isNotFound ());
    }

    @Test
    void umalistaGETComWhiskyValidoChamadaEStatusDeOKERetornado () throws Exception {
        // given
        WhiskyDTO whiskyDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();

        //when
        when (whiskyService.listAll ()).thenReturn (Collections.singletonList (whiskyDTO));

        // then
        mockMvc.perform (MockMvcRequestBuilders.get (WHISKY_API_URL_PATH)
                .contentType (MediaType.APPLICATION_JSON))
                .andExpect (status ().isOk ())
                .andExpect (jsonPath ("$[0].name", is (whiskyDTO.getName ())))
                .andExpect (jsonPath ("$[0].brand", is (whiskyDTO.getBrand ())))
                .andExpect (jsonPath ("$[0].type", is (whiskyDTO.getType ().toString ())));
    }

    @Test
    void quandoUmaListaGETSemWhiskiesEChamadaEStatusDeOKDeveSerRetornado () throws Exception {
        // given
        WhiskyDTO whiskyDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();

        //when
        when (whiskyService.listAll ()).thenReturn (Collections.singletonList (whiskyDTO));

        // then
        mockMvc.perform (MockMvcRequestBuilders.get (WHISKY_API_URL_PATH)
                .contentType (MediaType.APPLICATION_JSON))
                .andExpect (status ().isOk ());
    }

    @Test
    void quandoDELETEChamadoComIdValidoEntaoSemEstoqueStatusERetornado () throws Exception {
        // given
        WhiskyDTO whiskyDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();

        //when
        doNothing ().when (whiskyService).deleteById (whiskyDTO.getId ());

        // then
        mockMvc.perform (MockMvcRequestBuilders.delete (WHISKY_API_URL_PATH +
                "/" + whiskyDTO.getId ())
                .contentType (MediaType.APPLICATION_JSON))
                .andExpect (status ().isNoContent ());
    }

    @Test
    void whenDELETEChamadoComIdInvalidoEntaoNotFoundStatusERetornado () throws Exception {
        //when
        doThrow (WhiskyNotFoundException.class).when (whiskyService).deleteById (INVALID_WHISKY_ID);

        // then
        mockMvc.perform (MockMvcRequestBuilders.delete (WHISKY_API_URL_PATH +
                "/" + INVALID_WHISKY_ID)
                .contentType (MediaType.APPLICATION_JSON))
                .andExpect (status ().isNotFound ());
    }

    @Test
    void quandoPATCHChamadoParaIncrementoDescontoEntaoOKStatusERetornado () throws Exception {
        WhiskyQuantityDTO whiskyQuantityDTO = WhiskyQuantityDTO.builder ()
                .quantity (10)
                .build ();

        WhiskyDTO whiskyDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();
        whiskyDTO.setQuantity (whiskyDTO.getQuantity () + whiskyQuantityDTO.getQuantity ());

        when (whiskyService.increment (VALID_WHISKY_ID, whiskyQuantityDTO.getQuantity ())).
                thenReturn (whiskyDTO);

        mockMvc.perform (MockMvcRequestBuilders.patch (WHISKY_API_URL_PATH +
                "/" + VALID_WHISKY_ID + WHISKY_API_SUBPATH_INCREMENT_URL)
                .contentType (MediaType.APPLICATION_JSON)
                .content (asJsonString (whiskyQuantityDTO))).andExpect (status ().isOk ())
                .andExpect (jsonPath ("$.name", is (whiskyDTO.getName ())))
                .andExpect (jsonPath ("$.brand", is (whiskyDTO.getBrand ())))
                .andExpect (jsonPath ("$.type", is (whiskyDTO.getType ().toString ())))
                .andExpect (jsonPath ("$.quantity", is (whiskyDTO.getQuantity ())));
    }

    @Test
    void quandoPATCHEChamadoParaIncrementoMaiorQueMaxBadRequestStatusERetornado () throws Exception {
        WhiskyQuantityDTO quantityDTO = WhiskyQuantityDTO.builder ()
                .quantity (30)
                .build ();

        WhiskyDTO beerDTO = WhiskyDTOBuilder.builder ().build ().toWhiskyDTO ();
        beerDTO.setQuantity (beerDTO.getQuantity () + quantityDTO.getQuantity ());

        when (whiskyService.increment (VALID_WHISKY_ID, quantityDTO.getQuantity ())).
                thenThrow (WhiskyStockExceededException.class);

        mockMvc.perform (MockMvcRequestBuilders.patch (WHISKY_API_URL_PATH +
                "/" + VALID_WHISKY_ID + WHISKY_API_SUBPATH_INCREMENT_URL)
                .contentType (MediaType.APPLICATION_JSON)
                .content (asJsonString (quantityDTO)))
                .andExpect (status ().isBadRequest ());
    }

    @Test
    void quandoPATCHChamadoComIdWhiskyInvalidoParaIncrementoNotFoundStatusERetornado () throws Exception {
        WhiskyQuantityDTO quantityDTO = WhiskyQuantityDTO.builder ()
                .quantity (30)
                .build ();

        when (whiskyService.increment (INVALID_WHISKY_ID, quantityDTO.getQuantity ())).
                thenThrow (WhiskyNotFoundException.class);
        mockMvc.perform (MockMvcRequestBuilders.patch (WHISKY_API_URL_PATH +
                "/" + INVALID_WHISKY_ID + WHISKY_API_SUBPATH_INCREMENT_URL)
                .contentType (MediaType.APPLICATION_JSON)
                .content (asJsonString (quantityDTO)))
                .andExpect (status ().isNotFound ());
    }


    // @Test
    // void quandoPATCHChamadoParaDecrescimoDescontoOKstatusERetornado() throws Exception {
    //    WhiskyQuantityDTO whiskQuantityDTO = WhiskyQuantityDTO.builder()
    //           .quantity(5)
    //           .build();
//
    //  WhiskyDTO whiskyDTO = WhiskyDTOBuilder.builder().build().toWhiskyDTO();
    //  whiskyDTO.setQuantity(whiskyDTO.getQuantity() + whiskQuantityDTO.getQuantity());
//
    //  when(whiskyService.decrement(VALID_WHISKY_ID, whiskQuantityDTO.getQuantity())).thenReturn(whiskyDTO);
//
    //  mockMvc.perform(MockMvcRequestBuilders.patch(WHISKY_API_URL_PATH +
    //          "/" + VALID_WHISKY_ID + WHISKY_API_SUBPATH_DECREMENT_URL)
    //          .contentType(MediaType.APPLICATION_JSON)
    //          .content(asJsonString(whiskQuantityDTO))).andExpect(status().isOk())
    //          .andExpect(jsonPath("$.name", is(whiskyDTO.getName())))
    //          .andExpect(jsonPath("$.brand", is(whiskyDTO.getBrand())))
    //          .andExpect(jsonPath("$.type", is(whiskyDTO.getType().toString())))
    //          .andExpect(jsonPath("$.quantity", is(whiskyDTO.getQuantity())));
//    }

    //  @Test
    // void whenPATCHIsCalledToDEcrementLowerThanZeroThenBadRequestStatusIsReturned() throws Exception {
    //      WhiskyQuantityDTO whiskyQuantityDTO = WhiskyQuantityDTO.builder()
    //             .quantity(60)
    //             .build();
//
    //   WhiskyDTO whiskyDTO = WhiskyDTOBuilder.builder().build().toWhiskyDTO();
    //   whiskyDTO.setQuantity(whiskyDTO.getQuantity() + whiskyQuantityDTO.getQuantity());
//
    //   when(whiskyService.decrement(VALID_WHISKY_ID, whiskyQuantityDTO.getQuantity())).
    //           thenThrow(WhiskyStockExceededException.class);
//
    //   mockMvc.perform(MockMvcRequestBuilders.patch(WHISKY_API_URL_PATH +
    //          "/" + VALID_WHISKY_ID + WHISKY_API_SUBPATH_DECREMENT_URL)
    //          .contentType(MediaType.APPLICATION_JSON)
    //         .content(asJsonString(whiskyQuantityDTO))).andExpect(status().isBadRequest());
//    }
    //  @Test
    // void whenPATCHIsCalledWithInvalidBeerIdToDecrementThenNotFoundStatusIsReturned() throws Exception {
    //    WhiskyQuantityDTO whiskyQuantityDTO = WhiskyQuantityDTO.builder()
    //           .quantity(5)
    //            .build();
//
    //    when(whiskyService.decrement(INVALID_WHISKY_ID, whiskyQuantityDTO.getQuantity())).
    //            thenThrow(WhiskyNotFoundException.class);
    //     mockMvc.perform(MockMvcRequestBuilders.patch(WHISKY_API_URL_PATH +
    //            "/" + INVALID_WHISKY_ID + WHISKY_API_SUBPATH_DECREMENT_URL)
    //           .contentType(MediaType.APPLICATION_JSON)
    //            .content(asJsonString(whiskyQuantityDTO)))
    //            .andExpect(status().isNotFound());
    //    }
}

