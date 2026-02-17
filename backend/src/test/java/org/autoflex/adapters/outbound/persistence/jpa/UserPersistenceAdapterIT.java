package org.autoflex.adapters.outbound.persistence.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.autoflex.domain.User;
import org.autoflex.domain.UserRole;
import org.autoflex.fixtures.UserFixture;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UserPersistenceAdapterIT {

  @Inject UserPersistenceAdapter adapter;
  @Inject EntityManager entityManager;

  @Test
  void findByEmail_shouldReturnUser_whenEmailExists() {
    Optional<User> result = adapter.findByEmail("adm@autoflex.com");

    assertTrue(result.isPresent());
    assertEquals("adm@autoflex.com", result.get().getEmail());
    assertTrue(result.get().isActive());
    assertTrue(result.get().getRoles().contains(UserRole.ADMIN));
  }

  @Test
  void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
    Optional<User> result = adapter.findByEmail("not-found-" + UUID.randomUUID() + "@test.com");

    assertTrue(result.isEmpty());
  }

  @Test
  void findByEmail_shouldReturnEmpty_whenEmailIsNull() {
    Optional<User> result = adapter.findByEmail(null);

    assertTrue(result.isEmpty());
  }

  @Test
  @Transactional
  void save_shouldInsertUser_whenValidDomain() {
    User user = createNewUser();

    User saved = adapter.save(user);
    entityManager.flush();

    assertNotNull(saved.getId());
    assertEquals(user.getEmail(), saved.getEmail());
    assertEquals(user.getPasswordHash(), saved.getPasswordHash());
    assertTrue(saved.isActive());
    assertNotNull(saved.getCreatedAt());
    assertFalse(saved.getRoles().isEmpty());
    assertTrue(saved.getRoles().contains(UserRole.USER));
  }

  @Test
  @Transactional
  void save_shouldPersistRoles_whenUserHasMultipleRoles() {
    User user = createNewUser();
    user.setRoles(Set.of(UserRole.ADMIN, UserRole.USER));

    User saved = adapter.save(user);
    entityManager.flush();

    Optional<User> persisted = adapter.findByEmail(user.getEmail());
    assertTrue(persisted.isPresent());
    assertEquals(saved.getId(), persisted.get().getId());
    assertTrue(persisted.get().getRoles().contains(UserRole.ADMIN));
    assertTrue(persisted.get().getRoles().contains(UserRole.USER));
  }

  @Test
  @Transactional
  void save_shouldFail_whenEmailAlreadyExists() {
    User user = createNewUser();
    user.setEmail("adm@autoflex.com");

    assertThrows(
        PersistenceException.class,
        () -> {
          adapter.save(user);
          entityManager.flush();
        });
  }

  @Test
  @Transactional
  void save_shouldFail_whenEmailIsNull() {
    User user = createNewUser();
    user.setEmail(null);

    assertThrows(
        PersistenceException.class,
        () -> {
          adapter.save(user);
          entityManager.flush();
        });
  }

  @Test
  @Transactional
  void save_shouldFail_whenPasswordHashIsNull() {
    User user = createNewUser();
    user.setPasswordHash(null);

    assertThrows(
        PersistenceException.class,
        () -> {
          adapter.save(user);
          entityManager.flush();
        });
  }

  @Test
  @Transactional
  void save_shouldInsertUser_whenRolesIsEmpty() {
    User user = createNewUser();
    user.setRoles(Set.of());

    User saved = assertDoesNotThrow(() -> adapter.save(user));
    entityManager.flush();

    assertNotNull(saved.getId());
    Optional<User> persisted = adapter.findByEmail(user.getEmail());
    assertTrue(persisted.isPresent());
    assertTrue(persisted.get().getRoles().isEmpty());
  }

  @Test
  @Transactional
  void save_shouldInsertUser_whenRolesIsNull() {
    User user = createNewUser();
    user.setRoles(null);

    User saved = assertDoesNotThrow(() -> adapter.save(user));
    entityManager.flush();

    assertNotNull(saved.getId());
    Optional<User> persisted = adapter.findByEmail(user.getEmail());
    assertTrue(persisted.isPresent());
    assertTrue(persisted.get().getRoles() == null || persisted.get().getRoles().isEmpty());
  }

  private static User createNewUser() {
    User user = UserFixture.createValidUser();
    user.setEmail("it-user-" + UUID.randomUUID() + "@test.com");
    user.setPasswordHash("hash-test-123");
    user.setFirstName("Integration");
    user.setLastName("Test");
    user.setRoles(Set.of(UserRole.USER));
    user.setActive(true);
    return user;
  }
}
