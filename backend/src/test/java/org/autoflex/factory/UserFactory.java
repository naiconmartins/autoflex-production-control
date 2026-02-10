package org.autoflex.factory;

import org.autoflex.web.dto.UserRequestDTO;
import org.autoflex.web.dto.UserResponseDTO;

import java.time.Instant;
import java.util.Set;

public class UserFactory {

    private static final String EMAIL = "joana@test.com";
    private static final String FIRST_NAME = "Joana";
    private static final String LAST_NAME = "Almeida";
    private static final String PASSWORD = "1234";

    public static UserRequestDTO createUserRequestDTO() {
        return new UserRequestDTO(EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, "ADMIN");
    }

    public static UserRequestDTO createCustomUserRequestDTO(String email, String firstName, String lastName, String password, String role) {
        return new UserRequestDTO(email, password, firstName, lastName, role);
    }

    public static UserResponseDTO createUserResponseDTO(Long id, String role) {
        return new UserResponseDTO(id, EMAIL, FIRST_NAME, LAST_NAME, Set.of(role), true, Instant.now());
    }

    public static String uniqueEmail() {
        return TestData.unique("user") + "@test.com";
    }
}
