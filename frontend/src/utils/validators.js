const onlyDigits = (value) => value?.replace(/\D/g, "") ?? "";
const isAllRepeatedDigits = (value) => /^(\d)\1+$/.test(value);

// Validação de CPF
export const validateCPF = (cpf) => {
  if (!cpf) return { valid: false, message: "CPF é obrigatório" };

  const cleaned = onlyDigits(cpf);

  if (cleaned.length !== 11) {
    return { valid: false, message: "CPF deve conter 11 dígitos" };
  }

  if (isAllRepeatedDigits(cleaned)) {
    return { valid: false, message: "CPF inválido (números repetidos)" };
  }

  const digits = cleaned.split("").map(Number);

  let sum = 0;
  for (let i = 0; i < 9; i++) {
    sum += digits[i] * (10 - i);
  }

  let remainder = sum % 11;
  const firstVerifier = remainder < 2 ? 0 : 11 - remainder;
  if (firstVerifier !== digits[9]) {
    return { valid: false, message: "CPF inválido" };
  }

  sum = 0;
  for (let i = 0; i < 10; i++) {
    sum += digits[i] * (11 - i);
  }

  remainder = sum % 11;
  const secondVerifier = remainder < 2 ? 0 : 11 - remainder;
  if (secondVerifier !== digits[10]) {
    return { valid: false, message: "CPF inválido" };
  }

  return { valid: true, cleaned };
};

// Validação de CNPJ
export const validateCNPJ = (cnpj) => {
  if (!cnpj) return { valid: false, message: "CNPJ é obrigatório" };

  const cleaned = onlyDigits(cnpj);

  if (cleaned.length !== 14) {
    return { valid: false, message: "CNPJ deve conter 14 dígitos" };
  }

  if (/^(\d)\1{13}$/.test(cleaned)) {
    return { valid: false, message: "CNPJ inválido (números repetidos)" };
  }

  const digits = cleaned.split("").map(Number);
  const weights1 = [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];
  const weights2 = [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];

  let sum = 0;
  for (let i = 0; i < 12; i++) {
    sum += digits[i] * weights1[i];
  }

  let remainder = sum % 11;
  const firstVerifier = remainder < 2 ? 0 : 11 - remainder;
  if (firstVerifier !== digits[12]) {
    return { valid: false, message: "CNPJ inválido" };
  }

  sum = 0;
  for (let i = 0; i < 13; i++) {
    sum += digits[i] * weights2[i];
  }

  remainder = sum % 11;
  const secondVerifier = remainder < 2 ? 0 : 11 - remainder;
  if (secondVerifier !== digits[13]) {
    return { valid: false, message: "CNPJ inválido" };
  }

  return { valid: true, cleaned };
};

// Validação de CPF ou CNPJ
export const validateCPForCNPJ = (value) => {
  if (!value) return { valid: false, message: "CPF/CNPJ é obrigatório" };

  const cleaned = onlyDigits(value);

  if (cleaned.length === 11) {
    return validateCPF(cleaned);
  } else if (cleaned.length === 14) {
    return validateCNPJ(cleaned);
  } else {
    return { valid: false, message: "CPF deve conter 11 dígitos ou CNPJ deve conter 14 dígitos" };
  }
};

// Validação de email
export const validateEmail = (email) => {
  if (!email) return { valid: false, message: "Email é obrigatório" };

  // Regex mais robusta para validação de email
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  if (!emailRegex.test(email)) {
    return { valid: false, message: "Email deve ter um formato válido (ex: usuario@exemplo.com)" };
  }

  // Validações adicionais
  if (email.length > 255) {
    return { valid: false, message: "Email não pode ter mais de 255 caracteres" };
  }

  const parts = email.split("@");
  if (parts[0].length > 64) {
    return { valid: false, message: "Parte local do email (antes do @) não pode ter mais de 64 caracteres" };
  }

  return { valid: true };
};

// Validação de telefone
export const validatePhone = (phone) => {
  if (!phone) return { valid: false, message: "Telefone é obrigatório" };

  const cleaned = phone.replace(/\D/g, "");

  if (cleaned.length < 10 || cleaned.length > 11) {
    return { valid: false, message: "Telefone deve conter 10 ou 11 dígitos (incluindo DDD)" };
  }

  // Validação adicional: DDD deve estar entre 11 e 99
  const ddd = cleaned.slice(0, 2);
  if (parseInt(ddd) < 11 || parseInt(ddd) > 99) {
    return { valid: false, message: "DDD inválido" };
  }

  return { valid: true, cleaned };
};

export const PASSWORD_POLICY_REGEX =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

export const PASSWORD_POLICY_MESSAGE =
  "A senha deve ter no mínimo 8 caracteres, uma letra maiúscula, uma minúscula, um número e um caractere especial (@$!%*?&)";

export const evaluatePasswordRules = (password = "") => {
  const minLength = password.length >= 8;
  const hasUppercase = /[A-Z]/.test(password);
  const hasLowercase = /[a-z]/.test(password);
  const hasNumber = /\d/.test(password);
  const hasSpecial = /[@$!%*?&]/.test(password);
  const hasOnlyAllowedChars = /^[A-Za-z\d@$!%*?&]*$/.test(password);

  return {
    minLength,
    hasUppercase,
    hasLowercase,
    hasNumber,
    hasSpecial,
    hasOnlyAllowedChars,
    isValid:
      minLength &&
      hasUppercase &&
      hasLowercase &&
      hasNumber &&
      hasSpecial &&
      hasOnlyAllowedChars,
  };
};

export const validateStrongPassword = (password) => {
  if (!password || !password.trim()) {
    return { valid: false, message: "A senha é obrigatória" };
  }

  const rules = evaluatePasswordRules(password);
  if (!rules.hasOnlyAllowedChars) {
    return {
      valid: false,
      message: "Use apenas letras, números e os caracteres especiais @$!%*?&",
    };
  }

  if (!PASSWORD_POLICY_REGEX.test(password)) {
    return { valid: false, message: PASSWORD_POLICY_MESSAGE };
  }

  return { valid: true };
};

export const translateProfileName = (name) => {
  const nameLower = (name || "").toLowerCase();
  if (nameLower.includes("admin") || nameLower.includes("administrator")) {
    return "Administrador";
  }
  if (nameLower.includes("attendant") || nameLower.includes("atendente")) {
    return "Atendente";
  }
  if (nameLower.includes("evaluator") || nameLower.includes("avaliador")) {
    return "Avaliador";
  }
  return name || "Usuário";
};
