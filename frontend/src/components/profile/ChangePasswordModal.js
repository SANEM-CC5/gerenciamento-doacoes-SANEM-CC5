"use client";
import React, { useState, useEffect } from "react";
import { FaKey, FaTimes } from "react-icons/fa";
import apiService from "../../services/api";
import { useApi } from "../../hooks/useApi";
import { useNotification } from "../notifications/NotificationProvider";
import { validateStrongPassword } from "../../utils/validators";
import { PasswordInput, PasswordStrengthChecklist } from "../password/PasswordInput";
import styles from "./changePasswordModal.module.css";

const INITIAL_FORM = {
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
};

export default function ChangePasswordModal({ isOpen, onClose }) {
  const { loading, execute, clearError } = useApi();
  const { showNotification } = useNotification();

  const [formData, setFormData] = useState(INITIAL_FORM);
  const [fieldErrors, setFieldErrors] = useState({});
  const [serverError, setServerError] = useState("");

  useEffect(() => {
    if (!isOpen) return;

    setFormData(INITIAL_FORM);
    setFieldErrors({});
    setServerError("");
    clearError();
  }, [isOpen, clearError]);

  useEffect(() => {
    if (!isOpen) return;

    const handleKeyDown = (event) => {
      if (event.key === "Escape" && !loading) {
        onClose();
      }
    };

    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [isOpen, loading, onClose]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    setServerError("");
    if (fieldErrors[name]) {
      setFieldErrors((prev) => ({ ...prev, [name]: "" }));
    }
  };

  const validate = () => {
    const errors = {};

    if (!formData.currentPassword) {
      errors.currentPassword = "Senha atual é obrigatória";
    }

    const strongCheck = validateStrongPassword(formData.newPassword);
    if (!strongCheck.valid) {
      errors.newPassword = strongCheck.message;
    } else if (formData.currentPassword && formData.newPassword === formData.currentPassword) {
      errors.newPassword = "A nova senha deve ser diferente da senha atual";
    }

    if (!formData.confirmPassword) {
      errors.confirmPassword = "Confirmação da nova senha é obrigatória";
    } else if (formData.newPassword !== formData.confirmPassword) {
      errors.confirmPassword = "A confirmação da nova senha não confere";
    }

    return errors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setServerError("");

    const errors = validate();
    if (Object.keys(errors).length > 0) {
      setFieldErrors(errors);
      return;
    }

    try {
      await execute(() =>
        apiService.changeMyPassword({
          currentPassword: formData.currentPassword,
          newPassword: formData.newPassword,
          confirmPassword: formData.confirmPassword,
        })
      );
      showNotification("Senha alterada com sucesso!", "success");
      onClose();
    } catch (err) {
      const message = err.message || "Erro ao alterar senha";
      setServerError(message);
      showNotification(message, "error");
    }
  };

  return (
    <div
      className={styles.modalOverlay}
      onClick={() => {
        if (!loading) onClose();
      }}
      role="dialog"
      aria-modal="true"
      aria-labelledby="change-password-modal-title"
    >
      <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
        <div className={styles.modalHeader}>
          <h2 id="change-password-modal-title" className={styles.modalTitle}>
            <FaKey className={styles.titleIcon} size={18} />
            Alterar Senha
          </h2>
          <button
            type="button"
            className={styles.closeButton}
            onClick={onClose}
            disabled={loading}
            aria-label="Fechar janela"
          >
            <FaTimes />
          </button>
        </div>

        <p className={styles.modalDescription}>
          Informe sua senha atual e defina uma nova senha forte para proteger sua conta.
        </p>

        {serverError && <div className={styles.serverError}>{serverError}</div>}

        <form onSubmit={handleSubmit} noValidate>
          <PasswordInput
            id="currentPassword"
            name="currentPassword"
            label="Senha atual *"
            value={formData.currentPassword}
            onChange={handleChange}
            placeholder="Digite sua senha atual"
            autoComplete="current-password"
            error={fieldErrors.currentPassword}
            disabled={loading}
            required
          />

          <PasswordInput
            id="newPassword"
            name="newPassword"
            label="Nova senha *"
            value={formData.newPassword}
            onChange={handleChange}
            placeholder="Digite a nova senha"
            autoComplete="new-password"
            error={fieldErrors.newPassword}
            disabled={loading}
            required
          />

          <PasswordInput
            id="confirmPassword"
            name="confirmPassword"
            label="Confirmar nova senha *"
            value={formData.confirmPassword}
            onChange={handleChange}
            placeholder="Repita a nova senha"
            autoComplete="new-password"
            error={fieldErrors.confirmPassword}
            disabled={loading}
            required
          />

          <PasswordStrengthChecklist
            password={formData.newPassword}
            confirmPassword={formData.confirmPassword}
          />

          <div className={styles.modalActions}>
            <button
              type="button"
              className={styles.cancelButton}
              onClick={onClose}
              disabled={loading}
            >
              Cancelar
            </button>
            <button
              type="submit"
              className={styles.submitButton}
              disabled={loading}
            >
              {loading ? "Salvando..." : "Salvar Nova Senha"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
