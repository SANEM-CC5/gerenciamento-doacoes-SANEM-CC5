package com.javalovers.modules.appuser.infrastructure.persistence.repository;

import com.javalovers.modules.appuser.domain.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
  Optional<AppUser> findByLogin(String login);
}
