package ru.grishuchkov.cloudfilestorage.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.grishuchkov.cloudfilestorage.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findAll(Pageable pageable);

    Optional<User> findUserByLogin(String login);
}

