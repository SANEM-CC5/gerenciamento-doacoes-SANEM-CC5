"use client";
import Image from "next/image";
import Link from "next/link";
import { Suspense, useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { FaArrowLeft } from "react-icons/fa";
import authService from "../../services/authService";
import { useApi } from "../../hooks/useApi";
import { useNotification } from "../../components/notifications/NotificationProvider";
import { validateStrongPassword } from "../../utils/validators";
import { PasswordInput, PasswordStrengthChecklist } from "../../components/password/PasswordInput";
import styles from "../esqueci-minha-senha/recuperacao.module.css";

function RedefinirSenhaForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const token = searchParams.get("token") || "";

  const { loading, execute, clearError } = useApi();
  const { showNotification } = useNotification();

  const [checkingToken, setCheckingToken] = useState(true);
  const [tokenValid, setTokenValid] = useState(false);
  const [tokenError, setTokenError] = useState("");
  const [formData, setFormData] = useState({
    newPassword: "",
    confirmPassword: "",
  });
  const [fieldErrors, setFieldErrors] = useState({});
  const [serverError, setServerError] = useState("");
  const [completed, setCompleted] = useState(false);

  useEffect(() => {
    let active = true;

    const verify = async () => {
      if (!token.trim()) {
        if (active) {
          setTokenValid(false);
          setTokenError("Link de recuperação ausente ou inválido.");
          setCheckingToken(false);
        }
        return;
      }

      try {
        setCheckingToken(true);
        const result = await authService.validateResetToken(token.trim());
        if (!active) return;

        if (result?.valid) {
          setTokenValid(true);
          setTokenError("");
        } else {
          setTokenValid(false);
          setTokenError("Este link de recuperação é inválido ou expirou.");
        }
      } catch (err) {
        if (!active) return;
        setTokenValid(false);
        setTokenError(err.message || "Não foi possível validar o link de recuperação.");
      } finally {
        if (active) {
          setCheckingToken(false);
        }
      }
    };

    verify();
    return () => {
      active = false;
    };
  }, [token]);

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
    const strongCheck = validateStrongPassword(formData.newPassword);
    if (!strongCheck.valid) {
      errors.newPassword = strongCheck.message;
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
    clearError();

    const errors = validate();
    if (Object.keys(errors).length > 0) {
      setFieldErrors(errors);
      return;
    }

    try {
      await execute(() =>
        authService.resetPassword(token.trim(), formData.newPassword, formData.confirmPassword)
      );
      setCompleted(true);
      showNotification("Senha redefinida com sucesso! Faça login com sua nova senha.", "success");
      setTimeout(() => {
        router.push("/");
      }, 1500);
    } catch (err) {
      const message = err.message || "Não foi possível redefinir a senha.";
      setServerError(message);
      showNotification(message, "error");
    }
  };

  if (checkingToken) {
    return <div className={styles.infoBox}>Validando link de recuperação...</div>;
  }

  if (!tokenValid) {
    return (
      <>
        <div className={styles.errorMsg}>{tokenError}</div>
        <Link
          href="/esqueci-minha-senha"
          className={styles.button}
        >
          Solicitar Novo Link
        </Link>
        <Link href="/" className={styles.backLink}>
          <FaArrowLeft size={12} /> Voltar para o Login
        </Link>
      </>
    );
  }

  if (completed) {
    return (
      <>
        <div className={styles.successBox}>
          Senha redefinida com sucesso! Redirecionando para a tela de login...
        </div>
        <Link href="/" className={styles.button}>
          Ir para o Login
        </Link>
      </>
    );
  }

  return (
    <>
      <p className={styles.subtitle}>
        Defina sua nova senha de acesso ao sistema SANEM.
      </p>

      {serverError && <div className={styles.errorMsg}>{serverError}</div>}

      <form className={styles.form} onSubmit={handleSubmit} noValidate>
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

        <button type="submit" className={styles.button} disabled={loading}>
          {loading ? "Redefinindo..." : "Redefinir Senha"}
        </button>
      </form>

      <Link href="/" className={styles.backLink}>
        <FaArrowLeft size={12} /> Voltar para o Login
      </Link>
    </>
  );
}

export default function RedefinirSenhaPage() {
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
        <h1 className={styles.title}>Redefinir Senha</h1>

        <Suspense fallback={<div className={styles.infoBox}>Carregando...</div>}>
          <RedefinirSenhaForm />
        </Suspense>
      </div>
    </div>
  );
}
