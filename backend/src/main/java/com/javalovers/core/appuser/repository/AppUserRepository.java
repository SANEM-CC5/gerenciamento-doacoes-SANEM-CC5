package com.javalovers.core.appuser.repository;

import com.javalovers.core.appuser.domain.entity.AppUser;
import com.javalovers.core.status.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
  Optional<AppUser> findByLogin(String login);

  /** Usuários ativos e não excluídos cujo perfil (case-insensitive) está na lista informada (em minúsculas). */
  @Query("SELECT u FROM AppUser u WHERE u.status = :status AND u.deletedAt IS NULL " +
         "AND u.profile.deletedAt IS NULL AND LOWER(u.profile.name) IN :profileNames")
  List<AppUser> findActiveByProfileNames(@Param("status") Status status,
                                         @Param("profileNames") Collection<String> profileNames);
}
