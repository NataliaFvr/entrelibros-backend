package com.uade.entrelibros.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    public void enviarEmail(String para, String asunto, String texto) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(para);
        mensaje.setSubject(asunto);
        mensaje.setText(texto);
        mailSender.send(mensaje);
    }

    public void enviarCodigoVerificacion(String para, String nombre, String codigo) {
        enviarEmail(
                para,
                "Código de verificación - EntreLibros",
                "Hola " + nombre + ", tu código de verificación para EntreLibros es: " + codigo
                        + ". Este código vence en 15 minutos.");
    }
    public void enviarCodigoResetPassword(String para, String nombre, String codigo) {
    enviarEmail(
            para,
            "Recuperación de contraseña - EntreLibros",
            "Hola " + nombre + ", tu código para cambiar la contraseña en EntreLibros es: " + codigo
                    + ". Este código vence en 15 minutos.");
}
}