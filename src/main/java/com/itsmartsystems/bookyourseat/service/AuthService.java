package com.itsmartsystems.bookyourseat.service;

import com.itsmartsystems.bookyourseat.dto.BookingSummaryResponse;
import com.itsmartsystems.bookyourseat.dto.ChangeNewPasswordRequest;
import com.itsmartsystems.bookyourseat.dto.ChangePasswordRequest;
import com.itsmartsystems.bookyourseat.dto.EmailRequest;
import com.itsmartsystems.bookyourseat.dto.LoginRequest;
import com.itsmartsystems.bookyourseat.dto.RegisterRequest;
import com.itsmartsystems.bookyourseat.dto.UpdateCurrentPasswordRequest;
import com.itsmartsystems.bookyourseat.dto.UpdatePhoneRequest;
import com.itsmartsystems.bookyourseat.dto.UserDetails;
import com.itsmartsystems.bookyourseat.model.Department;
import com.itsmartsystems.bookyourseat.model.PostgresUser;
import com.itsmartsystems.bookyourseat.model.User;
import com.itsmartsystems.bookyourseat.repository.DepartmentRepository;
import com.itsmartsystems.bookyourseat.repository.PostgresUserRepository;
import com.itsmartsystems.bookyourseat.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UserSyncService userSyncService;
    private final PostgresUserRepository postgresUserRepository;
    private final DepartmentRepository departmentRepository;
    private final ReservationService reservationService;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            EmailService emailService,
            UserSyncService userSyncService,
            PostgresUserRepository postgresUserRepository,
            DepartmentRepository departmentRepository,
            ReservationService reservationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.userSyncService = userSyncService;
        this.postgresUserRepository = postgresUserRepository;
        this.departmentRepository = departmentRepository;
        this.reservationService = reservationService;
    }

    private boolean checkPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        String specialChars = "!@#$%&*";
        int counter = 0;
        for (char c : password.toCharArray()) {
            if (specialChars.indexOf(c) != -1) {
                counter++;
            }
        }

        return counter >= 2 && password.length() >= 10;
    }

    public void register(RegisterRequest request) {
        Optional<User> user0 = userRepository.findByEmail(request.getEmail());
        if (user0.isPresent()) {
            throw new IllegalArgumentException("Email already registered !");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password must not be empty !");
        }
        if (!checkPassword(request.getPassword())) {
            throw new IllegalArgumentException("Password must contain at least 2 special chars and to be >= 10 chars");
        }

        String crypted = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getName(), request.getEmail(), crypted, request.getRole(), false);
        User savedUser = userRepository.save(user);
        userSyncService.syncUser(savedUser);
    }

    public boolean login(LoginRequest request) {
        Optional<User> u = userRepository.findByEmail(request.getEmail());
        if (u.isEmpty()) {
            throw new IllegalArgumentException("User doesnt exist !");
        }

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        if (u.get().isFirstLog()) {
            throw new IllegalArgumentException("Password must be changed before login !");
        }

        handleFirstLogin(u.get());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpRequest = attr.getRequest();
        HttpServletResponse httpResponse = attr.getResponse();
        SecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.saveContext(context, httpRequest, httpResponse);

        return false;
    }

    public void changePassword(ChangePasswordRequest request) {
        Optional<User> u = userRepository.findByEmail(request.getEmail());
        if (u.isEmpty()) {
            throw new IllegalArgumentException("Email isnt registered !");
        }
        if (request.getOldPassword() == null || request.getOldPassword().isEmpty()) {
            throw new IllegalArgumentException("OldPassword must not be empty !");
        }
        if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            throw new IllegalArgumentException("NewPassword must not be empty !");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), u.get().getPassword())) {
            throw new IllegalArgumentException("Passwords doesnt match !");
        }
        if (!checkPassword(request.getNewPassword())) {
            throw new IllegalArgumentException("Password must contain at least 2 special chars and to be >= 10 chars");
        }

        handleFirstLogin(u.get());

        User existingUser = u.get();
        existingUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        existingUser.setFirstLog(false);
        User savedUser = userRepository.save(existingUser);
        userSyncService.syncUser(savedUser);
    }

    public void emailRequestforChanging(EmailRequest request) throws MessagingException {
        Optional<User> u = userRepository.findByEmail(request.getEmail());
        if (u.isEmpty()) {
            throw new IllegalArgumentException("User field is empty !");
        }

        String token = String.valueOf(UUID.randomUUID());
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
        User user = u.get();
        user.setToken(token);
        user.setTokenExpiresAt(expiresAt);
        User savedUser = userRepository.save(user);
        userSyncService.syncUser(savedUser);
        emailService.emailToSend(user.getEmail(), token);
    }

    public void handleFirstLogin(User user) {
        if (!user.isFirstLog()) {
            return;
        }

        userSyncService.syncUser(user);
        user.setFirstLog(false);
        User savedUser = userRepository.save(user);
        userSyncService.syncUser(savedUser);
    }

    public void forgotPassword(ChangeNewPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getcNewPassword())) {
            throw new IllegalArgumentException("Passwords must be the same !");
        }
        if (!checkPassword(request.getcNewPassword())) {
            throw new IllegalArgumentException("Password must contain at least 2 special chars and to be >= 10 chars !");
        }

        Optional<User> u = userRepository.findByToken(request.token());
        if (u.isEmpty()) {
            throw new IllegalArgumentException("User doesnt exist !");
        }
        if (LocalDateTime.now().isAfter(u.get().getTokenExpiresAt())) {
            throw new IllegalArgumentException("Expired session !");
        }

        User user = u.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setToken(null);
        User savedUser = userRepository.save(user);
        userSyncService.syncUser(savedUser);
    }

    public UserDetails UserDet() {
        User user = getAuthenticatedUser();
        PostgresUser postgresUser = postgresUserRepository.findByEmail(user.getEmail()).orElse(null);
        Integer postgresUserId = postgresUser == null ? null : postgresUser.getId();
        UserDetails details = new UserDetails(user.getId(), postgresUserId, user.getName(), user.getEmail(), user.getRole());
        if (postgresUser != null) {
            details.setPhoneNumber(postgresUser.getPhoneNumber());
            details.setDepartmentId(postgresUser.getDepartmentId());
            if (postgresUser.getDepartmentId() != null) {
                departmentRepository.findById(Long.valueOf(postgresUser.getDepartmentId()))
                        .map(Department::getName)
                        .ifPresent(details::setDepartmentName);
            }
        }

        return details;
    }

    public UserDetails updateCurrentUserPhone(UpdatePhoneRequest request) {
        User user = getAuthenticatedUser();
        PostgresUser postgresUser = postgresUserRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("PostgreSQL user does not exist!"));

        postgresUser.setPhoneNumber(request.getPhoneNumber().trim());
        postgresUserRepository.save(postgresUser);
        return UserDet();
    }

    public void updateCurrentUserPassword(UpdateCurrentPasswordRequest request) {
        User user = getAuthenticatedUser();
        String newPassword = request.getNewPassword();

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("Parola noua trebuie sa fie diferita de parola actuala!");
        }
        if (!checkPassword(newPassword)) {
            throw new IllegalArgumentException("Password must contain at least 10 characters and 2 special characters (!@#$%&*)!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setFirstLog(false);
        User savedUser = userRepository.save(user);
        userSyncService.syncUser(savedUser);
    }

    public BookingSummaryResponse getCurrentUserBookingSummary() {
        User user = getAuthenticatedUser();
        PostgresUser postgresUser = postgresUserRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("PostgreSQL user does not exist!"));
        return reservationService.getBookingSummary(postgresUser.getId());
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("User is not authenticated !");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User doesnt exist !"));
    }
}

