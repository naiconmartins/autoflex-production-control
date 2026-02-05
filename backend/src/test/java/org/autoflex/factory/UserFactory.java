package org.autoflex.factory;

import org.autoflex.web.dto.UserRequestDTO;

public class UserFactory {

    private static final String EMAIL = "joana@test.com";
    private static final String FIRST_NAME = "Joana";
    private static final String LAST_NAME = "Almeida";
    private static final String PASSWORD = "1234";

    public static UserRequestDTO createUserRequestDTO() {
        return new UserRequestDTO(EMAIL, FIRST_NAME, LAST_NAME, PASSWORD, "ADMIN");
    }
}
