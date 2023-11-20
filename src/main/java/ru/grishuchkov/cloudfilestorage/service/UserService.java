package ru.grishuchkov.cloudfilestorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.grishuchkov.cloudfilestorage.dto.UserRegistration;
import ru.grishuchkov.cloudfilestorage.entity.Role;
import ru.grishuchkov.cloudfilestorage.entity.User;
import ru.grishuchkov.cloudfilestorage.repository.UserRepository;
import ru.grishuchkov.cloudfilestorage.util.mapper.UserRegisterMapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public final class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRegisterMapper userRegisterMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserEntity(username);
        List<SimpleGrantedAuthority> authorities = getAuthorities(user);

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(), user.getPassword(), authorities);
    }

    private User getUserEntity(String username) {
        Optional<User> userOptional = userRepository.findUserByLogin(username);
        if(userOptional.isEmpty()){
            throw new RuntimeException("User not found");
        }
        return userOptional.get();
    }

    public List<SimpleGrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name())).toList();
    }

    public void registration(UserRegistration userRegistration){
        User user = userRegisterMapper.userRegistrationToUser(userRegistration);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.addRole(Role.USER_ROLE);

        userRepository.save(user);
    }
}
