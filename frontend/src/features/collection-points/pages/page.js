"use client";
import React from "react";
import MenuBar from "@/shared/components/menubar/menubar";
import Navigation from "@/shared/components/navegation/navegation";
import { useRouter } from "next/navigation";
import styles from "@/features/collection-points/styles/pontoscoleta.module.css";
import apiService from "@/shared/services/api";
import { useApi } from "@/shared/hooks/useApi";
import { useNotification } from "@/shared/components/notifications/NotificationProvider";
import PontoColetaForm from "@/features/collection-points/components/PontoColetaForm";

export default function CadastroPontoColeta() {
  const router = useRouter();
  const { loading, execute } = useApi();
  const { showNotification } = useNotification();

  const handleSubmit = async (payload) => {
    try {
      await execute(() => apiService.createCollectionPoint(payload));
      showNotification("Ponto de coleta cadastrado com sucesso!", "success");
      setTimeout(() => router.push("/pontoscoleta/lista"), 800);
    } catch (err) {
      showNotification(err.message || "Erro ao cadastrar ponto de coleta", "error");
    }
  };

  return (
    <div className={styles.containerGeral}>
      <MenuBar />
      <Navigation />
      <div className={styles.formWrapper}>
        <div className={styles.formContainer}>
          <h1 className={styles.titulo}>Cadastro de Ponto de Coleta</h1>
          <div className={styles.decoracao}></div>
          <PontoColetaForm
            onSubmit={handleSubmit}
            loading={loading}
            submitLabel="Cadastrar"
          />
        </div>
      </div>
    </div>
  );
}
