package one.digitalinnovation.whiskystock.mapper;

import one.digitalinnovation.whiskystock.dto.WhiskyDTO;
import one.digitalinnovation.whiskystock.entity.Whisky;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WhiskyMapper {

    WhiskyMapper INSTANCE = Mappers.getMapper (WhiskyMapper.class);

    Whisky toModel (WhiskyDTO whiskyDTO);

    WhiskyDTO toDTO (Whisky whisky);
}
