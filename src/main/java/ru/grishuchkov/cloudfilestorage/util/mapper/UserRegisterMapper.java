package ru.grishuchkov.cloudfilestorage.util.mapper;

import org.mapstruct.Mapper;
import ru.grishuchkov.cloudfilestorage.dto.UserRegistration;
import ru.grishuchkov.cloudfilestorage.entity.User;

@Mapper(componentModel = "spring")
public interface UserRegisterMapper {
    User userRegistrationToUser(UserRegistration userRegistration);
}
