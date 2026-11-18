"use client";
import { useState, useEffect, useRef } from 'react';
import styles from './menuBar.module.css';
import Image from 'next/image';
import authService from '../../../services/authService';
import { useRouter } from 'next/navigation';
import { useNotification } from '../../../components/notifications/NotificationProvider';
import ChangePasswordModal from '../../../components/profile/ChangePasswordModal';
import NotificationBell from '../../../components/notifications/NotificationBell';
import { useOnboarding } from '../../../components/onboarding/OnboardingProvider';
import { translateProfileName } from '../../../utils/validators';
import { FaUserCircle, FaKey, FaSignOutAlt, FaRoute } from 'react-icons/fa';

export default function MenuBar({ hasNotification }) {
  const [userData, setUserData] = useState({
    name: 'Usuário',
    email: '',
    role: '',
  });
  const [menuOpen, setMenuOpen] = useState(false);
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const dropdownRef = useRef(null);
  const router = useRouter();
  const { showNotification } = useNotification();
  const { startTourForCurrentPage } = useOnboarding();

  useEffect(() => {
    const user = authService.getUser();
    if (user) {
      setUserData({
        name: user.name || 'Usuário',
        email: user.email || '',
        role: user.role || '',
      });
    }
  }, []);

  useEffect(() => {
    if (!menuOpen) return;

    const handleOutsideClick = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setMenuOpen(false);
      }
    };

    const handleEscape = (event) => {
      if (event.key === 'Escape') {
        setMenuOpen(false);
      }
    };

    document.addEventListener('mousedown', handleOutsideClick);
    window.addEventListener('keydown', handleEscape);
    return () => {
      document.removeEventListener('mousedown', handleOutsideClick);
      window.removeEventListener('keydown', handleEscape);
    };
  }, [menuOpen]);

  const handleLogout = () => {
    setMenuOpen(false);
    authService.logout();
    showNotification("Logout realizado com sucesso!", "success");
    router.push('/');
  };

  const handleOpenProfile = () => {
    setMenuOpen(false);
    router.push('/perfil');
  };

  const handleOpenChangePassword = () => {
    setMenuOpen(false);
    setShowPasswordModal(true);
  };

  return (
    <>
      <header className={styles.menuBar}>
        <div className={styles.rightSection}>
          <div className={styles.userMenuContainer} ref={dropdownRef}>
            <button
              type="button"
              className={styles.userInfoButton}
              onClick={() => setMenuOpen((prev) => !prev)}
              aria-haspopup="menu"
              aria-expanded={menuOpen}
              title="Opções do usuário"
            >
              <UserIcon />
              <span className={styles.userName}>{userData.name}</span>
              <span className={`${styles.arrowDown} ${menuOpen ? styles.arrowOpen : ''}`}>▼</span>
            </button>

            {menuOpen && (
              <div className={styles.dropdownMenu} role="menu">
                <div className={styles.dropdownHeader}>
                  <span className={styles.dropdownName}>{userData.name}</span>
                  {userData.email && (
                    <span className={styles.dropdownEmail}>{userData.email}</span>
                  )}
                  {userData.role && (
                    <span className={styles.dropdownRole}>
                      {translateProfileName(userData.role)}
                    </span>
                  )}
                </div>

                <div className={styles.dropdownDivider} />

                <button
                  type="button"
                  role="menuitem"
                  className={styles.dropdownItem}
                  onClick={handleOpenProfile}
                >
                  <FaUserCircle className={styles.dropdownIcon} />
                  <span>Meu Perfil</span>
                </button>

                <button
                  type="button"
                  role="menuitem"
                  className={styles.dropdownItem}
                  onClick={handleOpenChangePassword}
                >
                  <FaKey className={styles.dropdownIcon} />
                  <span>Alterar Senha</span>
                </button>

                <div className={styles.dropdownDivider} />

                <button
                  type="button"
                  role="menuitem"
                  className={`${styles.dropdownItem} ${styles.dropdownItemDanger}`}
                  onClick={handleLogout}
                >
                  <FaSignOutAlt className={styles.dropdownIcon} />
                  <span>Sair</span>
                </button>
              </div>
            )}
          </div>

          <button
            type="button"
            className={styles.tourButton}
            onClick={startTourForCurrentPage}
            data-tour="tour-button"
            title="Ver o passo a passo desta tela"
          >
            <FaRoute aria-hidden="true" />
            <span>Tour</span>
          </button>
          <NotificationBell />
          <div className={styles.iconWrapper} onClick={handleLogout} title="Sair">
            <LogoutIcon />
          </div>
        </div>
      </header>

      <ChangePasswordModal
        isOpen={showPasswordModal}
        onClose={() => setShowPasswordModal(false)}
      />
    </>
  );
}

function UserIcon() {
    return (
        <Image
            src="/user-icon.png"
            alt="User"
            width={24}
            height={24}
            style={{ marginRight: 10 }}
        />
    );
}

function LogoutIcon() {
    return (
        <Image
            src="/logout-icon.png"
            alt="Logout"
            width={24}
            height={24}
            style={{ marginRight: 12 }}
        />
    );
}
