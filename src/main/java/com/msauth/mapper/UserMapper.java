package com.msauth.mapper;

import com.msauth.dto.request.UserRequestDto;
import com.msauth.dto.response.UserResponseDto;
import com.msauth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User requestDtoToUser(UserRequestDto userRequestDto);
    UserResponseDto userToResponseDto(User user);

}
