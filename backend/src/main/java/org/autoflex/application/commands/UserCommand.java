package org.autoflex.application.commands;

public record UserCommand(
    String email, String password, String firstName, String lastName, String role) {}
