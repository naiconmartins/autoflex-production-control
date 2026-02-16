package org.autoflex.application.commands;

public record InsertUserCommand(
    String email, String password, String firstName, String lastName, String role) {}
