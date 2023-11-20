package ru.grishuchkov.cloudfilestorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.grishuchkov.cloudfilestorage.dto.UserRegistration;
import ru.grishuchkov.cloudfilestorage.entity.User;
import ru.grishuchkov.cloudfilestorage.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
final class UserService implements UserDetailsService {

    private final UserRepository userRepository;

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
                .map(role -> new SimpleGrantedAuthority(role.getRole())).toList();
    }

    public void registration(UserRegistration userRegistration){

    }
}
