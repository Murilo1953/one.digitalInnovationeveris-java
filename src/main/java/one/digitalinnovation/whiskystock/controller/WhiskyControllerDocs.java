package one.digitalinnovation.whiskystock.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import one.digitalinnovation.whiskystock.dto.WhiskyDTO;
import one.digitalinnovation.whiskystock.exception.WhiskyAlreadyRegisteredException;
import one.digitalinnovation.whiskystock.exception.WhiskyNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Api("Manages whisky stock")
public interface WhiskyControllerDocs {

    @ApiOperation(value = "Whisky creation operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success whisky creation"),
            @ApiResponse(code = 400, message = "Missing required fields or wrong field range value.")
    })
    WhiskyDTO createWhisky(WhiskyDTO whiskyDTO) throws WhiskyAlreadyRegisteredException;

    @ApiOperation(value = "Returns whisky found by a given name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success whisky found in the system"),
            @ApiResponse(code = 404, message = "Whisky with given name not found.")
    })
    WhiskyDTO findByName(@PathVariable String name) throws WhiskyNotFoundException;

    @ApiOperation(value = "Returns a list of all whisky registered in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all whiskies registered in the system"),
    })
    List<WhiskyDTO> listWhisky();

    @ApiOperation(value = "Delete a whisky found by a given valid Id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success whisky deleted in the system"),
            @ApiResponse(code = 404, message = "Whisky with given id not found.")
    })
    void deleteById(@PathVariable Long id) throws WhiskyNotFoundException;
}
