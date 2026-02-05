package org.autoflex.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.autoflex.domain.entities.User;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class UserResponseDTO {
    public Long id;
    public String email;
    public String firstName;
    public String lastName;
    public Set<String> roles;
    public Boolean active;
    public Instant createdAt;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.roles = user.getRoles()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
        this.active = user.isActive();
        this.createdAt = user.getCreatedAt();
    }
}
