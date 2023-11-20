package ru.grishuchkov.cloudfilestorage.dto;

import lombok.Getter;
import lombok.Setter;
import ru.grishuchkov.cloudfilestorage.util.validate.PasswordMatches;

@Getter
@Setter
@PasswordMatches
public class UserRegistration {
    private String email;
    private String login;
    private String password;
    private String passwordConfirm;
}
