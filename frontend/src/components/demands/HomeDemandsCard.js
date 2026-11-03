"use client";
// RF20 – Destaque das demandas prioritárias ativas na Home
import { useEffect, useState } from "react";
import Link from "next/link";
import apiService from "../../services/api";
import authService from "../../services/authService";
import DemandProgress from "./DemandProgress";
import { PRIORITY_LABELS } from "./demandUtils";
import styles from "./homeDemandsCard.module.css";

const MAX_ITEMS = 3;

export default function HomeDemandsCard() {
  const [demands, setDemands] = useState(null);

  useEffect(() => {
    if (!authService.isAuthenticated()) return;
    apiService.getPriorityDemands({ status: "ACTIVE" })
      .then((data) => {
        const pending = (Array.isArray(data) ? data : []).filter((d) => !d.fulfilled);
        setDemands(pending.slice(0, MAX_ITEMS));
      })
      .catch(() => setDemands([]));
  }, []);

  if (!demands || demands.length === 0) return null;

  return (
    <section className={styles.card} aria-labelledby="homeDemandsTitle">
      <div className={styles.header}>
        <h2 id="homeDemandsTitle" className={styles.title}>Demandas prioritárias</h2>
        <Link href="/demandas" className={styles.link}>Ver todas</Link>
      </div>
      <ul className={styles.list}>
        {demands.map((d) => (
          <li key={d.priorityDemandId} className={styles.item}>
            <div className={styles.itemHeader}>
              <span className={styles.itemName}>{d.itemDescription}</span>
              <span className={`${styles.badge} ${styles[`p${d.priority}`]}`}>{PRIORITY_LABELS[d.priority]}</span>
            </div>
            <DemandProgress demand={d} compact />
          </li>
        ))}
      </ul>
    </section>
  );
}
