package com.itsmartsystems.bookyourseat.service;

import com.itsmartsystems.bookyourseat.model.Reservation;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private final JavaMailSender sender;

    public EmailService(JavaMailSender sender) {
        this.sender = sender;
    }

    public void emailToSend(String email, String token) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject("Forgot Password");
        String content = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "  body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }\n" +
                "  .container { background-color: #ffffff; border-radius: 8px; padding: 30px; max-width: 500px; margin: 0 auto; }\n" +
                "  .footer { margin-top: 30px; font-size: 12px; color: #888888; }\n" +
                "  .signature { margin-top: 30px; border-top: 1px solid #cccccc; padding-top: 20px; }\n" +
                "  .signature p { margin: 2px 0; font-size: 13px; color: #333333; }\n" +
                "  .signature .gray { color: #888888; }\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"container\">\n" +
                "    <h2>Resetare parolă</h2>\n" +
                "    <p>Ai solicitat resetarea parolei pentru contul tău Book Your Seat.</p>\n" +
                "    <p>Apasă butonul de mai jos pentru a-ți seta o parolă nouă. Link-ul este valabil 15 minute.</p>\n" +
                "    <a href=\"http://localhost:5173/change-password?token=" + token + "\" " +
                "style=\"display:inline-block; background-color:#2563eb; color:#ffffff; padding:12px 24px; text-decoration:none; border-radius:6px; margin-top:20px; font-weight:bold;\">Resetează parola</a>\n" +
                "    <p class=\"footer\">Dacă nu ai solicitat această acțiune, poți ignora acest email.</p>\n" +
                "    <div class=\"signature\">\n" +
                "      <img src=\"cid:logoImage\" width=\"150\"><br><br>\n" +
                "      <p><strong>Tomulescu Eduard</strong></p>\n" +
                "      <p class=\"gray\">Mobile +40 749 856 745</p>\n" +
                "      <p><a href=\"mailto:tomulescu.eduard11@gmail.com\">tomulescu.eduard11@gmail.com</a></p>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>";
        helper.setText(content, true);
        helper.addInline("logoImage", new ClassPathResource("static/preview.png"));
        sender.send(message);
    }

    public void sendReservationConfirmation(String email, Reservation reservation) {
        if (email == null || email.isBlank() || reservation == null) {
            return;
        }

        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Confirmare rezervare - Book Your Seat");

            String roomName = reservation.getRoom() != null ? reservation.getRoom().getName() :
                    (reservation.getSeat() != null && reservation.getSeat().getRoom() != null ? reservation.getSeat().getRoom().getName() : "Sala");
            String seatCode = reservation.getSeat() != null ? reservation.getSeat().getCode() : "toată sala";
            String start = reservation.getStartDateTime() == null ? "-" : reservation.getStartDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            String end = reservation.getEndDateTime() == null ? "-" : reservation.getEndDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

            String content = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<style>\n" +
                    "  body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }\n" +
                    "  .container { background-color: #ffffff; border-radius: 10px; padding: 30px; max-width: 560px; margin: 0 auto; }\n" +
                    "  .title { color: #1f2937; font-size: 26px; margin-bottom: 12px; }\n" +
                    "  .label { font-weight: bold; color: #374151; }\n" +
                    "  .value { color: #111827; }\n" +
                    "  .box { background: #f8fafc; border: 1px solid #e5e7eb; border-radius: 8px; padding: 14px; margin-top: 16px; }\n" +
                    "  .footer { margin-top: 24px; font-size: 12px; color: #6b7280; }\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "  <div class=\"container\">\n" +
                    "    <div class=\"title\">Rezervare confirmată</div>\n" +
                    "    <p>Bună ziua,</p>\n" +
                    "    <p>Rezervarea ta a fost înregistrată cu succes în Book Your Seat.</p>\n" +
                    "    <div class=\"box\">\n" +
                    "      <div><span class=\"label\">Sala:</span> <span class=\"value\">" + roomName + "</span></div>\n" +
                    "      <div><span class=\"label\">Loc:</span> <span class=\"value\">" + seatCode + "</span></div>\n" +
                    "      <div><span class=\"label\">Început:</span> <span class=\"value\">" + start + "</span></div>\n" +
                    "      <div><span class=\"label\">Sfârșit:</span> <span class=\"value\">" + end + "</span></div>\n" +
                    "    </div>\n" +
                    "    <p class=\"footer\">Mulțumim că folosești Book Your Seat.</p>\n" +
                    "  </div>\n" +
                    "</body>\n" +
                    "</html>";

            helper.setText(content, true);
            helper.addInline("logoImage", new ClassPathResource("static/preview.png"));
            sender.send(message);
            System.out.println("Reservation confirmation email sent to: " + email);
        } catch (MessagingException e) {
            System.err.println("Failed to send reservation confirmation email to " + email + ": " + e.getMessage());
        }
    }
}