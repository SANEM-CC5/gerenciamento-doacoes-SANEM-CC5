package com.javalovers.core.appuser.service;

import com.javalovers.core.appuser.domain.dto.request.LoginRequestDTO;
import com.javalovers.core.appuser.domain.dto.response.LoginResponseDTO;
import com.javalovers.core.appuser.domain.entity.AppUser;
import com.javalovers.core.appuser.repository.AppUserRepository;
import com.javalovers.common.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AppUserRepository appUserRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public LoginResponseDTO authenticate(LoginRequestDTO loginRequest) {
      String identifier = loginRequest.getLogin() != null ? loginRequest.getLogin().trim() : "";
      AppUser user = appUserRepository.findByLoginAndDeletedAtIsNull(identifier)
          .or(() -> appUserRepository.findByLoginIgnoreCaseAndDeletedAtIsNull(identifier))
          .or(() -> appUserRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(identifier))
          .orElseThrow(() -> new RuntimeException("Usuário ou senha incorretos"));

      if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
          throw new RuntimeException("Usuário ou senha incorretos");
      }

      if (user.getStatus() != com.javalovers.core.status.Status.ACTIVE) {
          throw new RuntimeException("Usuário inativo");
      }

      String token = jwtService.generateToken(user.getLogin());

      return new LoginResponseDTO(
          token,
          user.getName(),
          user.getEmail(),
          user.getProfile().getName(),
          user.getUserId()
      );
  }

  public AppUser getCurrentUser() {
      var auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth == null || auth.getName() == null) {
          throw new RuntimeException("Usuário não autenticado");
      }
      String login = auth.getName();
      return appUserRepository.findByLoginAndDeletedAtIsNull(login)
          .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
  }

  public boolean validateToken(String token) {
    try {
        jwtService.extractLogin(token);
        return true;
    } catch (Exception e) {
        return false;
    }
  }
}
