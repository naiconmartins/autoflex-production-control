package org.autoflex.adapters.inbound.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.autoflex.adapters.inbound.dto.request.LoginRequestDTO;
import org.autoflex.adapters.inbound.dto.response.LoginResponseDTO;
import org.autoflex.application.commands.LoginCommand;
import org.autoflex.application.dto.TokenData;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class LoginMapperTest {

  private final LoginMapper mapper = Mappers.getMapper(LoginMapper.class);

  @Test
  void toCommand_shouldMapLoginRequestDtoToCommand() {
    LoginRequestDTO dto = new LoginRequestDTO("email@test.com", "secret123");

    LoginCommand command = mapper.toCommand(dto);

    assertEquals(dto.email, command.email());
    assertEquals(dto.password, command.password());
  }

  @Test
  void toResponse_shouldMapTokenDataToLoginResponseDto() {
    TokenData data = new TokenData("jwt-token", 3600);

    LoginResponseDTO response = mapper.toResponse(data);

    assertEquals("jwt-token", response.accessToken);
    assertEquals(3600, response.expires);
    assertEquals("Bearer", response.tokenType);
  }
}
