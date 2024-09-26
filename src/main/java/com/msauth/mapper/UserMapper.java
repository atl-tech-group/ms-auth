package com.msauth.mapper;

import com.msauth.dto.request.RegisterRequestDto;
import com.msauth.dto.response.RegisterResponseDto;
import com.msauth.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = SPRING,
        unmappedTargetPolicy = IGNORE,
        unmappedSourcePolicy = IGNORE)
public interface UserMapper {

    UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "password", ignore = true)
    UserEntity buildUserEntity(RegisterRequestDto request);

    RegisterResponseDto buildRegisterResponseDto(UserEntity entity);
}
