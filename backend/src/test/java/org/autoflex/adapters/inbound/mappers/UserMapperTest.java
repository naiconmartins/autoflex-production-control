package org.autoflex.adapters.inbound.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.autoflex.adapters.inbound.dto.request.UserRequestDTO;
import org.autoflex.application.commands.UserCommand;
import org.autoflex.fixtures.UserFixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UserMapperTest {

  private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

  @Test
  void toInsertUserCommand_shouldMapUserRequestDtoToCommand() {
    UserRequestDTO dto = UserFixture.createValidUserRequestDTO();

    UserCommand command = mapper.toInsertUserCommand(dto);

    assertEquals(dto.email, command.email());
    assertEquals(dto.password, command.password());
    assertEquals(dto.firstName, command.firstName());
    assertEquals(dto.lastName, command.lastName());
    assertEquals(dto.role, command.role());
  }
}
