package com.itsmartsystems.bookyourseat.config;

import com.itsmartsystems.bookyourseat.repository.UserRepository;
import com.itsmartsystems.bookyourseat.service.UserSyncService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UserSyncRunner implements CommandLineRunner {
    private final UserRepository userRepository;
    private final UserSyncService userSyncService;

    public UserSyncRunner(UserRepository userRepository, UserSyncService userSyncService) {
        this.userRepository = userRepository;
        this.userSyncService = userSyncService;
    }

    @Override
    public void run(String... args) {
        userSyncService.syncUsers(userRepository.findAll());
    }
}