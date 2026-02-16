package org.autoflex.adapters.inbound.mappers;

import org.autoflex.adapters.inbound.dto.request.UserRequestDTO;
import org.autoflex.application.commands.InsertUserCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface UserMapper {

  InsertUserCommand toInsertUserCommand(UserRequestDTO dto);
}
