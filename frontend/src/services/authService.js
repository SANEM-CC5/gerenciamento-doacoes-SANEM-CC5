// Serviço de autenticação
import { API_CONFIG } from '../config/api';

class AuthService {
  constructor() {
    this.tokenKey = 'auth_token';
    this.userKey = 'user_data';
    this.baseURL = API_CONFIG.BASE_URL;
  }

  isAuthenticated() {
    return !!this.getToken();
  }

  getToken() {
    if (typeof window !== 'undefined') {
      return localStorage.getItem(this.tokenKey);
    }
    return null;
  }

  getUser() {
    if (typeof window !== 'undefined') {
      const userData = localStorage.getItem(this.userKey);
      return userData ? JSON.parse(userData) : null;
    }
    return null;
  }

  setAuthData(token, user) {
    if (typeof window !== 'undefined') {
      localStorage.setItem(this.tokenKey, token);
      localStorage.setItem(this.userKey, JSON.stringify(user));
    }
  }

  clearAuthData() {
    if (typeof window !== 'undefined') {
      localStorage.removeItem(this.tokenKey);
      localStorage.removeItem(this.userKey);
    }
  }

  async postPublicAuth(endpoint, payload, defaultErrorMessage) {
    try {
      const response = await fetch(`${this.baseURL}${endpoint}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      const data = await response.json().catch(() => ({}));

      if (!response.ok) {
        const message =
          data?.details ||
          data?.message ||
          (data?.errors ? Object.values(data.errors).join('; ') : null) ||
          data?.error ||
          defaultErrorMessage;
        throw new Error(message);
      }

      return data;
    } catch (error) {
      if (error.message === 'Failed to fetch' || error.name === 'TypeError') {
        throw new Error(
          `Não foi possível conectar ao servidor. Verifique se o backend está rodando em ${this.baseURL}`
        );
      }
      throw error;
    }
  }

  async login(username, password) {
    if (!username || !password) {
      throw new Error('Usuário e senha são obrigatórios');
    }

    const data = await this.postPublicAuth(
      '/auth/login',
      { login: username, password },
      'Usuário ou senha incorretos'
    );

    const user = {
      id: data.userId,
      name: data.name,
      email: data.email,
      role: data.role,
      perfilId: null,
    };

    this.setAuthData(data.token, user);

    return { success: true, user, token: data.token };
  }

  async forgotPassword(email) {
    return this.postPublicAuth(
      '/auth/forgot-password',
      { email },
      'Não foi possível processar a solicitação de recuperação de senha'
    );
  }

  async validateResetToken(token) {
    return this.postPublicAuth(
      '/auth/reset-password/validate',
      { token },
      'Não foi possível validar o link de recuperação'
    );
  }

  async resetPassword(token, newPassword, confirmPassword) {
    return this.postPublicAuth(
      '/auth/reset-password',
      { token, newPassword, confirmPassword },
      'Não foi possível redefinir a senha'
    );
  }

  logout() {
    this.clearAuthData();
    if (typeof window !== 'undefined') {
      window.location.href = '/';
    }
  }

  async validateToken() {
    const token = this.getToken();
    if (!token) return false;

    try {
      const response = await fetch(`${this.baseURL}/auth/validate`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        this.clearAuthData();
        return false;
      }

      return true;
    } catch (error) {
      console.error('Erro na validação do token:', error);
      return false;
    }
  }

  getAuthHeader() {
    const token = this.getToken();
    return token ? { Authorization: `Bearer ${token}` } : {};
  }
}

const authService = new AuthService();
export default authService;
