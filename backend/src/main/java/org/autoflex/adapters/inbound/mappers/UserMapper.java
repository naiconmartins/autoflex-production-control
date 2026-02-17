package org.autoflex.adapters.inbound.mappers;

import org.autoflex.adapters.inbound.dto.request.UserRequestDTO;
import org.autoflex.application.commands.UserCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface UserMapper {

  UserCommand toInsertUserCommand(UserRequestDTO dto);
}
