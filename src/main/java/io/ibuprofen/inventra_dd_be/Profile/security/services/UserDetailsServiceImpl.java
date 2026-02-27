package io.ibuprofen.inventra_dd_be.Profile.security.services;

import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String cleanEmail = email.trim();
        System.out.println("Trying to load user by email: [" + cleanEmail + "]");
        User user = userRepository.findByEmailIgnoreCase(cleanEmail)
                .orElseThrow(() -> {
                    System.out.println("User not found with email: [" + cleanEmail + "]");
                    return new UsernameNotFoundException("User Not Found with email: " + cleanEmail);
                });

        System.out.println("User found: " + user.getName() + " with role: " + user.getRole());
        return UserDetailsImpl.build(user);
    }
}
