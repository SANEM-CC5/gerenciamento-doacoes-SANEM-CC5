"use client";
import React, { useState } from "react";
import { FaEye, FaEyeSlash, FaCheckCircle, FaRegCircle, FaExclamationCircle } from "react-icons/fa";
import { evaluatePasswordRules } from "../../utils/validators";
import styles from "./password.module.css";

export function PasswordInput({
  id,
  name,
  label,
  value,
  onChange,
  placeholder,
  error,
  autoComplete,
  required = false,
  disabled = false,
}) {
  const [visible, setVisible] = useState(false);

  return (
    <div className={styles.fieldGroup}>
      {label && (
        <label htmlFor={id} className={styles.fieldLabel}>
          {label}
        </label>
      )}
      <div className={styles.passwordWrapper}>
        <input
          id={id}
          name={name}
          type={visible ? "text" : "password"}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          autoComplete={autoComplete}
          required={required}
          disabled={disabled}
          className={`${styles.input} ${error ? styles.inputError : ""}`}
        />
        <button
          type="button"
          className={styles.toggleButton}
          onClick={() => setVisible((prev) => !prev)}
          aria-label={visible ? "Ocultar senha" : "Mostrar senha"}
          tabIndex={-1}
        >
          {visible ? <FaEyeSlash /> : <FaEye />}
        </button>
      </div>
      {error && <span className={styles.errorText}>{error}</span>}
    </div>
  );
}

export function PasswordStrengthChecklist({ password = "", confirmPassword }) {
  const rules = evaluatePasswordRules(password);
  const items = [
    { key: "minLength", label: "Mínimo de 8 caracteres", met: rules.minLength },
    { key: "hasUppercase", label: "Pelo menos 1 letra maiúscula", met: rules.hasUppercase },
    { key: "hasLowercase", label: "Pelo menos 1 letra minúscula", met: rules.hasLowercase },
    { key: "hasNumber", label: "Pelo menos 1 número", met: rules.hasNumber },
    { key: "hasSpecial", label: "Pelo menos 1 caractere especial (@$!%*?&)", met: rules.hasSpecial },
  ];

  const showMismatch =
    typeof confirmPassword === "string" &&
    confirmPassword.length > 0;
  const passwordsMatch = showMismatch && password === confirmPassword;

  return (
    <div className={styles.checklist}>
      <div className={styles.checklistTitle}>Requisitos da senha:</div>
      <ul className={styles.checklistItems}>
        {items.map((item) => (
          <li
            key={item.key}
            className={`${styles.checklistItem} ${item.met ? styles.checklistItemMet : ""}`}
          >
            {item.met ? <FaCheckCircle size={13} /> : <FaRegCircle size={13} />}
            <span>{item.label}</span>
          </li>
        ))}
        {!rules.hasOnlyAllowedChars && (
          <li className={`${styles.checklistItem} ${styles.checklistItemError}`}>
            <FaExclamationCircle size={13} />
            <span>Contém caractere especial não permitido (use apenas @$!%*?&)</span>
          </li>
        )}
        {showMismatch && (
          <li
            className={`${styles.checklistItem} ${
              passwordsMatch ? styles.checklistItemMet : styles.checklistItemError
            }`}
          >
            {passwordsMatch ? <FaCheckCircle size={13} /> : <FaExclamationCircle size={13} />}
            <span>
              {passwordsMatch ? "As senhas conferem" : "As senhas não conferem"}
            </span>
          </li>
        )}
      </ul>
    </div>
  );
}
