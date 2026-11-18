"use client";
import Image from "next/image";
import Link from "next/link";
import { useState } from "react";
import { FaArrowLeft } from "react-icons/fa";
import authService from "../../services/authService";
import { useApi } from "../../hooks/useApi";
import { useNotification } from "../../components/notifications/NotificationProvider";
import { validateEmail } from "../../utils/validators";
import styles from "./recuperacao.module.css";

export default function EsqueciMinhaSenhaPage() {
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");
  const [submittedMessage, setSubmittedMessage] = useState("");
  const { loading, execute, clearError } = useApi();
  const { showNotification } = useNotification();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    clearError();

    const validation = validateEmail(email.trim());
    if (!validation.valid) {
      setError(validation.message);
      return;
    }

    try {
      const response = await execute(() => authService.forgotPassword(email.trim()));
      const message =
        response?.message ||
        "Se o e-mail estiver cadastrado e ativo, você receberá as instruções para redefinir sua senha.";
      setSubmittedMessage(message);
      showNotification("Solicitação enviada! Verifique seu e-mail.", "success");
    } catch (err) {
      const message = err.message || "Erro ao solicitar recuperação de senha.";
      setError(message);
      showNotification(message, "error");
    }
  };

  return (
    <div className={styles.page}>
      <div className={styles.cardBox}>
        <div className={styles.logoContainer}>
          <Image
            src="/logo-sanem.svg"
            alt="Logo SANEM"
            width={110}
            height={110}
            className={styles.logo}
          />
        </div>

        <h1 className={styles.title}>Recuperar Senha</h1>

        {submittedMessage ? (
          <>
            <div className={styles.successBox}>
              <p>{submittedMessage}</p>
              <p style={{ marginTop: 8, fontSize: "0.86rem" }}>
                O link de redefinição expira em 30 minutos e pode ser utilizado apenas uma vez.
              </p>
            </div>
            <Link href="/" className={styles.button}>
              Voltar para o Login
            </Link>
            <button
              type="button"
              className={styles.secondaryButton}
              onClick={() => {
                setSubmittedMessage("");
                setEmail("");
                setError("");
              }}
            >
              Solicitar para outro e-mail
            </button>
          </>
        ) : (
          <>
            <p className={styles.subtitle}>
              Informe o e-mail cadastrado na sua conta para receber o link de redefinição de senha.
            </p>

            <form className={styles.form} onSubmit={handleSubmit} noValidate>
              <div>
                <label htmlFor="email" className={styles.label}>
                  E-mail cadastrado
                </label>
                <input
                  id="email"
                  name="email"
                  type="email"
                  placeholder="usuario@exemplo.com"
                  value={email}
                  onChange={(e) => {
                    setEmail(e.target.value);
                    if (error) setError("");
                  }}
                  autoComplete="email"
                  className={styles.input}
                  disabled={loading}
                  required
                />
              </div>

              <button type="submit" className={styles.button} disabled={loading}>
                {loading ? "Enviando..." : "Enviar instruções"}
              </button>
            </form>

            {error && <div className={styles.errorMsg}>{error}</div>}

            <Link href="/" className={styles.backLink}>
              <FaArrowLeft size={12} /> Voltar para o Login
            </Link>
          </>
        )}
      </div>
    </div>
  );
}
