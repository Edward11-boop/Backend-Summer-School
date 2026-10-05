package com.itsmartsystems.bookyourseat.security;
import com.itsmartsystems.bookyourseat.model.User;
import com.itsmartsystems.bookyourseat.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class CustomUserDetailsService implements UserDetailsService{

            private final UserRepository userRepository ;

            public CustomUserDetailsService(UserRepository userRepository)
            {

                this.userRepository = userRepository;
            }

            public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                Optional<User> user = userRepository.findByEmail(email);
                if(user.isEmpty()) throw new UsernameNotFoundException("User not found with email : " + email );
                else {
                    return org.springframework.security.core.userdetails.User.builder()
                            .username(user.get().getEmail())
                            .password(user.get().getPassword())
                            .authorities(user.get().getRole().name())
                            .build();
                }



            }
}
