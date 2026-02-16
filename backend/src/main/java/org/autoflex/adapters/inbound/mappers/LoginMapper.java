package org.autoflex.adapters.inbound.mappers;

import org.autoflex.adapters.inbound.dto.request.LoginRequestDTO;
import org.autoflex.adapters.inbound.dto.response.LoginResponseDTO;
import org.autoflex.application.commands.LoginCommand;
import org.autoflex.application.dto.TokenData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface LoginMapper {

  LoginCommand toCommand(LoginRequestDTO dto);

  @Mapping(source = "token", target = "accessToken")
  @Mapping(source = "expiresIn", target = "expires")
  LoginResponseDTO toResponse(TokenData data);
}
