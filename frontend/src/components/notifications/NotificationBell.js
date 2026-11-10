"use client";
import { useCallback, useEffect, useRef, useState } from "react";
import { useRouter } from "next/navigation";
import { FaBell, FaBoxOpen, FaCheckDouble } from "react-icons/fa";
import apiService, { DATA_CHANGED_EVENT } from "../../services/api";
import authService from "../../services/authService";
import { useNotification } from "./NotificationProvider";
import styles from "./notificationBell.module.css";

// RF19 – Sino de notificações: mostra alertas de estoque mínimo do usuário logado.
const POLL_INTERVAL_MS = 30000;

function formatDate(value) {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "";
  return date.toLocaleString("pt-BR", {
    day: "2-digit", month: "2-digit", hour: "2-digit", minute: "2-digit",
  });
}

export default function NotificationBell() {
  const router = useRouter();
  const { showNotification } = useNotification();
  const [open, setOpen] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const previousCount = useRef(null);
  const wrapperRef = useRef(null);

  const refreshCount = useCallback(async () => {
    if (!authService.isAuthenticated()) return;
    try {
      const data = await apiService.getUnreadNotificationCount();
      const count = Number(data?.unreadCount ?? 0);
      // Avisa com um toast quando chegam novas notificações (não no primeiro carregamento)
      if (previousCount.current !== null && count > previousCount.current) {
        const diff = count - previousCount.current;
        showNotification(
          diff === 1
            ? "Novo alerta: um item atingiu o estoque mínimo."
            : `${diff} novos alertas de estoque mínimo.`,
          "warning"
        );
      }
      previousCount.current = count;
      setUnreadCount(count);
    } catch {
      // silencioso: o sino não deve quebrar a página se o backend oscilar
    }
  }, [showNotification]);

  const loadList = useCallback(async () => {
    setLoading(true);
    try {
      const data = await apiService.getNotifications({ limit: 20 });
      setItems(Array.isArray(data) ? data : []);
    } catch {
      setItems([]);
    } finally {
      setLoading(false);
    }
  }, []);

  // Polling + recarrega ao voltar para a aba + após qualquer escrita na API
  useEffect(() => {
    refreshCount();
    const interval = setInterval(refreshCount, POLL_INTERVAL_MS);
    const onFocus = () => refreshCount();
    window.addEventListener("focus", onFocus);
    window.addEventListener(DATA_CHANGED_EVENT, onFocus);
    return () => {
      clearInterval(interval);
      window.removeEventListener("focus", onFocus);
      window.removeEventListener(DATA_CHANGED_EVENT, onFocus);
    };
  }, [refreshCount]);

  // Fecha ao clicar fora / ESC
  useEffect(() => {
    if (!open) return;
    const onClick = (e) => {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target)) setOpen(false);
    };
    const onKey = (e) => e.key === "Escape" && setOpen(false);
    document.addEventListener("mousedown", onClick);
    document.addEventListener("keydown", onKey);
    return () => {
      document.removeEventListener("mousedown", onClick);
      document.removeEventListener("keydown", onKey);
    };
  }, [open]);

  const toggle = () => {
    const next = !open;
    setOpen(next);
    if (next) loadList();
  };

  // markNotification* são PATCH e disparam DATA_CHANGED_EVENT, que já atualiza o contador
  const handleClickItem = async (n) => {
    if (!n.read) {
      try {
        await apiService.markNotificationAsRead(n.notificationId);
        setItems((prev) => prev.map((x) =>
          x.notificationId === n.notificationId ? { ...x, read: true } : x));
      } catch { /* ignora */ }
    }
    setOpen(false);
    router.push("/estoque?estoqueBaixo=1");
  };

  const handleMarkAll = async () => {
    try {
      await apiService.markAllNotificationsAsRead();
      setItems((prev) => prev.map((x) => ({ ...x, read: true })));
    } catch {
      showNotification("Erro ao marcar notificações como lidas", "error");
    }
  };

  const badgeLabel = unreadCount > 99 ? "99+" : String(unreadCount);

  return (
    <div className={styles.wrapper} ref={wrapperRef}>
      <button
        type="button"
        className={styles.bellButton}
        onClick={toggle}
        aria-haspopup="true"
        aria-expanded={open}
        aria-label={unreadCount > 0
          ? `Notificações: ${unreadCount} não lida(s)`
          : "Notificações"}
        title="Notificações"
      >
        <FaBell size={20} />
        {unreadCount > 0 && <span className={styles.badge}>{badgeLabel}</span>}
      </button>

      {open && (
        <div className={styles.dropdown} role="dialog" aria-label="Notificações">
          <div className={styles.header}>
            <span className={styles.headerTitle}>Notificações</span>
            {unreadCount > 0 && (
              <button type="button" className={styles.markAll} onClick={handleMarkAll}>
                <FaCheckDouble /> Marcar todas como lidas
              </button>
            )}
          </div>

          <ul className={styles.list}>
            {loading ? (
              <li className={styles.empty}>Carregando...</li>
            ) : items.length === 0 ? (
              <li className={styles.empty}>Nenhuma notificação.</li>
            ) : (
              items.map((n) => (
                <li key={n.notificationId}>
                  <button
                    type="button"
                    className={`${styles.item} ${n.read ? "" : styles.unread}`}
                    onClick={() => handleClickItem(n)}
                  >
                    <span className={styles.itemIcon}><FaBoxOpen /></span>
                    <span className={styles.itemBody}>
                      <span className={styles.itemTitle}>{n.title}</span>
                      <span className={styles.itemMessage}>{n.message}</span>
                      <span className={styles.itemMeta}>
                        {formatDate(n.createdAt)}
                        {n.alertOpen
                          ? <span className={styles.tagOpen}>Saldo atual: {n.currentStock ?? "-"}</span>
                          : <span className={styles.tagResolved}>Reposto</span>}
                      </span>
                    </span>
                  </button>
                </li>
              ))
            )}
          </ul>

          <button
            type="button"
            className={styles.footer}
            onClick={() => { setOpen(false); router.push("/estoque?estoqueBaixo=1"); }}
          >
            Ver itens com estoque baixo
          </button>
        </div>
      )}
    </div>
  );
}
