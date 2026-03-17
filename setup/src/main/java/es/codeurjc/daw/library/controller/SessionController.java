package es.codeurjc.daw.library.controller;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import javax.sql.rowset.serial.SerialBlob;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.daw.library.model.Equipo;
import es.codeurjc.daw.library.repository.EquipoRepository;
import es.codeurjc.daw.library.service.EquipoService;

@Controller
public class SessionController {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private EquipoService equipoService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/loginerror")
    public String loginerror(Model model) {
        model.addAttribute("error", true);
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String nombreEquipo,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model, @RequestParam("image") MultipartFile image) throws IOException, SQLException {

        // 1. Verify that the passwords match
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "register";
        }

        // 2. Validate that the user does not already exist
        if (equipoRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "El nombre de usuario ya está en uso.");
            return "register";
        }

        if (image.isEmpty()) {
            model.addAttribute("error", "La foto del equipo es obligatoria");
            return "register";
        }

        if (equipoRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "El email ya está en uso.");
            return "register";
        }

        // 3. Create the new user and encrypt the password
        String encodedPassword = passwordEncoder.encode(password);

        // The team is initialized with the "USER" role
        Equipo nuevoEquipo = new Equipo(username, email, encodedPassword, nombreEquipo, "USER");

        byte[] bytes = image.getBytes();
        Blob blob = new SerialBlob(bytes);
        nuevoEquipo.setImagen(blob);
        nuevoEquipo.setHasImagen(true);

        equipoRepository.save(nuevoEquipo);

        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(HttpServletRequest request, Model model, @RequestParam String email) {
        String token = UUID.randomUUID().toString();
        try {
            equipoService.updateResetPasswordToken(token, email);
            
            String resetLink = "https://localhost:8443/reset-password?token=" + token;
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Enlace para restablecer contraseña de FutbolManager");
            message.setText("Hola,\n\nHas solicitado restablecer tu contraseña. Haz clic en el siguiente enlace para cambiarla:\n" + resetLink);
            
            mailSender.send(message);
            model.addAttribute("message", "Te hemos enviado un enlace a tu correo.");
            
        } catch (Exception e) {
            e.printStackTrace(); 
            model.addAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
        }
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam(value = "token") String token, Model model) {
        Optional<Equipo> equipoOpt = equipoService.getByResetPasswordToken(token);
        if (equipoOpt.isEmpty()) {
            model.addAttribute("error", "Enlace inválido o caducado.");
            return "login"; // Redirigir o mostrar vista de error
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token, @RequestParam String password, Model model) {
        Optional<Equipo> equipoOpt = equipoService.getByResetPasswordToken(token);
        if (equipoOpt.isPresent()) {
            String encodedPassword = passwordEncoder.encode(password);
            equipoService.updatePassword(equipoOpt.get(), encodedPassword);
            model.addAttribute("message", "Has cambiado tu contraseña exitosamente.");
        } else {
            model.addAttribute("error", "Token inválido.");
        }
        return "login";
    }
}
