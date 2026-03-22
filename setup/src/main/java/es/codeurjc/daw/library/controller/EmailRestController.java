package es.codeurjc.daw.library.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.daw.library.service.TeamService;
import es.codeurjc.daw.library.dto.ForgotPasswordRequestDTO;
import es.codeurjc.daw.library.dto.MessageResponseDTO;

@RestController
@RequestMapping("/api/v1/email")
public class EmailRestController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TeamService teamService;

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponseDTO> sendResetPasswordEmail(@RequestBody ForgotPasswordRequestDTO requestDTO) {
        
        String email = requestDTO.email();

        // Basic validation
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("El campo email es obligatorio."));
        }

        try {
            // 1. Generate the token
            String token = UUID.randomUUID().toString();
            
            // 2. Save the token associated with the team
            teamService.updateResetPasswordToken(token, email);
            
            // 3. Prepare the link
            String resetLink = "https://localhost:8443/reset-password?token=" + token;
            
            // 4. Configure and send the email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Enlace para restablecer contraseña de FutbolManager");
            message.setText("Hola,\n\nHas solicitado restablecer tu contraseña. Haz clic en el siguiente enlace para cambiarla:\n" + resetLink);
            
            mailSender.send(message);
            
            // 5. Return HTTP 200 OK response with our DTO
            return ResponseEntity.ok(new MessageResponseDTO("Te hemos enviado un enlace a tu correo."));
            
        } catch (Exception e) {
            e.printStackTrace();
            // Return HTTP 500 with error message using the DTO
            return ResponseEntity.internalServerError().body(new MessageResponseDTO("Error al procesar la solicitud: " + e.getMessage()));
        }
    }
}