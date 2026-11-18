"use client";
import Image from "next/image";
import Link from "next/link";
import styles from "./page.module.css";
import { useState } from "react";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import authService from "../services/authService";
import { useApi } from "../hooks/useApi";
import { useNotification } from "../components/notifications/NotificationProvider";

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const { loading, execute, clearError } = useApi();
  const { showNotification } = useNotification();

  async function handleSubmit(e) {
    e.preventDefault();
    clearError();
    setError("");

    const trimmedUser = username.trim();
    if (!trimmedUser || !password) {
      setError("Usuário/e-mail e senha são obrigatórios.");
      return;
    }

    try {
      await execute(() => authService.login(trimmedUser, password));
      showNotification("Login realizado com sucesso!", "success");
      setTimeout(() => {
        window.location.href = "/home";
      }, 500);
    } catch (err) {
      const message = err.message || "Usuário ou senha incorretos.";
      showNotification(message, "error");
      setError(message);
    }
  }

  return (
    <div className={styles.page}>
      <div className={styles.loginBox}>
        <div className={styles.logoContainer}>
          <Image src="/logo-sanem.svg" alt="Logo SANEM" width={120} height={120} className={styles.logo} />
        </div>
        <h2 className={styles.loginTitle}>Login</h2>
        <form className={styles.loginForm} onSubmit={handleSubmit}>
          <input
            id="username"
            name="username"
            type="text"
            placeholder="Usuário ou e-mail"
            value={username}
            onChange={(e) => {
              setUsername(e.target.value);
              setError("");
            }}
            className={styles.input}
            autoComplete="username"
          />
          <div className={styles.passwordWrapper}>
            <input
              id="password"
              name="password"
              type={showPassword ? "text" : "password"}
              placeholder="Senha"
              value={password}
              onChange={(e) => {
                setPassword(e.target.value);
                setError("");
              }}
              className={styles.input}
              autoComplete="current-password"
            />
            <button
              type="button"
              className={styles.togglePassword}
              onClick={() => setShowPassword((prev) => !prev)}
              aria-label={showPassword ? "Ocultar senha" : "Mostrar senha"}
            >
              {showPassword ? <FaEyeSlash size={18} /> : <FaEye size={18} />}
            </button>
          </div>
          <button type="submit" className={styles.button} disabled={loading}>
            {loading ? "Entrando..." : "Login"}
          </button>
        </form>
        {error && <div className={styles.errorMsg}>{error}</div>}
        <Link href="/esqueci-minha-senha" className={styles.forgot}>
          Esqueci minha senha
        </Link>
      </div>
    </div>
  );
}
