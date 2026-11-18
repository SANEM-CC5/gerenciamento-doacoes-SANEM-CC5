package com.javalovers.core.appuser.service;

import com.javalovers.core.appuser.domain.entity.AppUser;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Serviço responsável pela entrega de instruções de recuperação de senha.
 * Utiliza SMTP quando configurado ou log de desenvolvimento como fallback.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetDeliveryService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${app.mail.from:noreply@sanem.local}")
    private String mailFrom;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.password-reset.expiration-minutes:30}")
    private long expirationMinutes;

    @Value("${app.password-reset.dev-log-fallback:true}")
    private boolean devLogFallback;

    public void sendPasswordResetEmail(AppUser user, String rawToken) {
        String resetUrl = buildResetUrl(rawToken);
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        boolean hasSmtpConfig = mailHost != null && !mailHost.isBlank() && mailSender != null;

        if (!hasSmtpConfig) {
            logDevResetLink(user.getEmail(), resetUrl, "SMTP não configurado");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setFrom(mailFrom);
            helper.setTo(user.getEmail());
            helper.setSubject("SANEM - Recuperação de Senha");
            helper.setText(buildEmailHtml(user.getName(), resetUrl), true);

            mailSender.send(message);
            log.info("E-mail de recuperação de senha enviado para {}", user.getEmail());
        } catch (Exception ex) {
            log.error("Falha ao enviar e-mail de recuperação para {}: {}", user.getEmail(), ex.getMessage());
            logDevResetLink(user.getEmail(), resetUrl, "erro ao enviar via SMTP");
        }
    }

    String buildResetUrl(String rawToken) {
        String baseUrl = frontendUrl != null ? frontendUrl.replaceAll("/+$", "") : "http://localhost:3000";
        String encodedToken = URLEncoder.encode(rawToken, StandardCharsets.UTF_8);
        return baseUrl + "/redefinir-senha?token=" + encodedToken;
    }

    private void logDevResetLink(String email, String resetUrl, String reason) {
        if (devLogFallback) {
            log.warn("[DEV-FALLBACK] ({}) Link de recuperação para {}: {}", reason, email, resetUrl);
        } else {
            log.warn("Envio de recuperação de senha indisponível para {} ({})", email, reason);
        }
    }

    private String buildEmailHtml(String name, String resetUrl) {
        String safeName = escapeHtml(name != null ? name : "Usuário");
        String safeUrl = escapeHtml(resetUrl);
        return """
                <div style="font-family: Arial, sans-serif; max-width: 560px; margin: 0 auto; color: #18132b;">
                  <h2 style="color: #1976d2;">Recuperação de Senha - SANEM</h2>
                  <p>Olá, <strong>%s</strong>,</p>
                  <p>Recebemos uma solicitação para redefinir a senha da sua conta no sistema SANEM.</p>
                  <p style="margin: 24px 0;">
                    <a href="%s" style="background-color: #1976d2; color: #ffffff; padding: 12px 24px; text-decoration: none; border-radius: 8px; font-weight: bold; display: inline-block;">
                      Redefinir Minha Senha
                    </a>
                  </p>
                  <p>Se o botão não funcionar, copie e cole este link no navegador:</p>
                  <p style="word-break: break-all; color: #1551b1;">%s</p>
                  <p style="font-size: 0.9rem; color: #555;">
                    Este link é válido por <strong>%d minutos</strong> e pode ser usado apenas uma vez.<br/>
                    Caso você não tenha solicitado esta alteração, ignore este e-mail.
                  </p>
                </div>
                """.formatted(safeName, safeUrl, safeUrl, expirationMinutes);
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
