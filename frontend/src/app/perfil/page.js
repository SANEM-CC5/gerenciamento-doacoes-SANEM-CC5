"use client";
import React, { useState, useEffect } from "react";
import { FaUser, FaEnvelope, FaIdBadge, FaShieldAlt, FaKey } from "react-icons/fa";
import ProtectedRoute from "../../components/ProtectedRoute";
import Navigation from "../components/navegation/navegation";
import MenuBar from "../components/menubar/menubar";
import ChangePasswordModal from "../../components/profile/ChangePasswordModal";
import apiService from "../../services/api";
import { mapUserFromBackend } from "../../services/dataMapper";
import { translateProfileName } from "../../utils/validators";
import { useNotification } from "../../components/notifications/NotificationProvider";
import styles from "./perfil.module.css";

function getInitials(name = "") {
  const parts = name.trim().split(/\s+/).filter(Boolean);
  if (parts.length === 0) return "U";
  if (parts.length === 1) return parts[0][0].toUpperCase();
  return `${parts[0][0]}${parts[parts.length - 1][0]}`.toUpperCase();
}

export default function PerfilPage() {
  const { showNotification } = useNotification();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showPasswordModal, setShowPasswordModal] = useState(false);

  useEffect(() => {
    const loadProfile = async () => {
      try {
        setLoading(true);
        setError("");
        const data = await apiService.getMyProfile();
        setProfile(mapUserFromBackend(data));
      } catch (err) {
        const message = err.message || "Erro ao carregar dados do perfil";
        setError(message);
        showNotification(message, "error");
      } finally {
        setLoading(false);
      }
    };

    loadProfile();
  }, [showNotification]);

  return (
    <ProtectedRoute>
      <div className={styles.containerGeral}>
        <Navigation />
        <MenuBar />
        <main className={styles.contentWrapper}>
          <div className={styles.pageInner}>
            <h1 className={styles.titulo}>Meu Perfil</h1>
            <div className={styles.decoracao} />

            {loading ? (
              <div className={styles.loadingMessage}>Carregando informações do perfil...</div>
            ) : error ? (
              <div className={styles.errorMessage}>{error}</div>
            ) : (
              <>
                <section className={styles.card}>
                  <div className={styles.profileHeader}>
                    <div className={styles.avatarCircle}>
                      {getInitials(profile?.nomeCompleto)}
                    </div>
                    <div className={styles.headerMeta}>
                      <h2 className={styles.userTitle}>{profile?.nomeCompleto}</h2>
                      <div className={styles.badgesRow}>
                        <span className={styles.roleBadge}>
                          {translateProfileName(profile?.perfil)}
                        </span>
                        <span
                          className={`${styles.statusBadge} ${
                            profile?.status === "ACTIVE"
                              ? styles.statusActive
                              : styles.statusInactive
                          }`}
                        >
                          {profile?.status === "ACTIVE" ? "Ativo" : "Inativo"}
                        </span>
                      </div>
                    </div>
                  </div>

                  <div className={styles.infoGrid}>
                    <div className={styles.infoItem}>
                      <span className={styles.infoLabel}>
                        <FaUser /> Nome Completo
                      </span>
                      <span className={styles.infoValue}>{profile?.nomeCompleto || "-"}</span>
                    </div>

                    <div className={styles.infoItem}>
                      <span className={styles.infoLabel}>
                        <FaIdBadge /> Login
                      </span>
                      <span className={styles.infoValue}>{profile?.login || "-"}</span>
                    </div>

                    <div className={styles.infoItem}>
                      <span className={styles.infoLabel}>
                        <FaEnvelope /> E-mail
                      </span>
                      <span className={styles.infoValue}>{profile?.email || "-"}</span>
                    </div>

                    <div className={styles.infoItem}>
                      <span className={styles.infoLabel}>
                        <FaShieldAlt /> Perfil de Acesso
                      </span>
                      <span className={styles.infoValue}>
                        {translateProfileName(profile?.perfil)}
                      </span>
                    </div>
                  </div>
                </section>

                <section className={styles.card}>
                  <h2 className={styles.sectionTitle}>
                    <FaKey style={{ color: "var(--color-primary)" }} />
                    Segurança da Conta
                  </h2>
                  <p className={styles.sectionDescription}>
                    Mantenha sua senha atualizada regularmente para garantir a segurança do seu acesso ao sistema.
                  </p>
                  <button
                    type="button"
                    className={styles.primaryButton}
                    onClick={() => setShowPasswordModal(true)}
                  >
                    <FaKey /> Alterar Senha
                  </button>
                </section>
              </>
            )}
          </div>
        </main>

        <ChangePasswordModal
          isOpen={showPasswordModal}
          onClose={() => setShowPasswordModal(false)}
        />
      </div>
    </ProtectedRoute>
  );
}
