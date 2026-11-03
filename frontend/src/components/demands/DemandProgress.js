"use client";
import styles from "./demandProgress.module.css";
import { progressTone } from "./demandUtils";

/** Barra "Recebido X de Y" (RF20). */
export default function DemandProgress({ demand, compact = false }) {
  const pct = Math.max(0, Math.min(100, demand.progressPercent ?? 0));
  const tone = progressTone(demand);
  return (
    <div className={compact ? styles.compact : styles.wrapper}>
      <div className={styles.labels}>
        <span className={styles.received}>
          Recebido <strong>{demand.quantityReceived}</strong> de <strong>{demand.quantityNeeded}</strong>
        </span>
        <span className={styles.percent}>{pct}%</span>
      </div>
      <div
        className={styles.track}
        role="progressbar"
        aria-valuemin={0}
        aria-valuemax={100}
        aria-valuenow={pct}
        aria-label={`Recebido ${demand.quantityReceived} de ${demand.quantityNeeded} unidades de ${demand.itemDescription}`}
      >
        <div className={`${styles.bar} ${styles[tone]}`} style={{ width: `${pct}%` }} />
      </div>
      {!compact && (
        <div className={styles.remaining}>
          {demand.fulfilled
            ? "Meta atingida"
            : `Faltam ${demand.quantityRemaining} unidade(s)`}
        </div>
      )}
    </div>
  );
}
