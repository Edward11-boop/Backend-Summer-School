package com.itsmartsystems.bookyourseat.controller;

import com.itsmartsystems.bookyourseat.dto.BookingSummaryResponse;
import com.itsmartsystems.bookyourseat.dto.ChangeNewPasswordRequest;
import com.itsmartsystems.bookyourseat.dto.ChangePasswordRequest;
import com.itsmartsystems.bookyourseat.dto.EmailRequest;
import com.itsmartsystems.bookyourseat.dto.LoginRequest;
import com.itsmartsystems.bookyourseat.dto.RegisterRequest;
import com.itsmartsystems.bookyourseat.dto.UpdateCurrentPasswordRequest;
import com.itsmartsystems.bookyourseat.dto.UpdatePhoneRequest;
import com.itsmartsystems.bookyourseat.dto.UserDetails;
import com.itsmartsystems.bookyourseat.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return "User registered successfully !";
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest request) {
        boolean mustChangePassword = authService.login(request);
        if (mustChangePassword) {
            return "Password must be changed !";
        }
        return "Logged successfully !";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) throws Exception {
        request.logout();
        return "Logged out successfully!";
    }

    @PutMapping("/change-password")
    public String changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return "Password has been successfully changed !";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@Valid @RequestBody EmailRequest emailRequest) throws MessagingException {
        authService.emailRequestforChanging(emailRequest);
        return "If the email exists you'll receive an email to change the password !";
    }

    @PutMapping("/reset-password")
    public String resetPassword(@Valid @RequestBody ChangeNewPasswordRequest request) {
        authService.forgotPassword(request);
        return "Successfully changing the password , next time note it ! :))";
    }

    @GetMapping("/me")
    public UserDetails getMe() {
        return authService.UserDet();
    }

    @PutMapping("/me/phone")
    public UserDetails updatePhone(@Valid @RequestBody UpdatePhoneRequest request) {
        return authService.updateCurrentUserPhone(request);
    }

    @PutMapping("/me/password")
    public String updatePassword(@Valid @RequestBody UpdateCurrentPasswordRequest request) {
        authService.updateCurrentUserPassword(request);
        return "Password has been successfully changed!";
    }

    @GetMapping("/me/reservations")
    public BookingSummaryResponse getMyReservations() {
        return authService.getCurrentUserBookingSummary();
    }
}
