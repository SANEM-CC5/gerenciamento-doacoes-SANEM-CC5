"use client";
import React from "react";
import MenuBar from "../components/menubar/menubar";
import Navigation from "../components/navegation/navegation";
import { useRouter } from "next/navigation";
import styles from "./pontoscoleta.module.css";
import apiService from "../../services/api";
import { useApi } from "../../hooks/useApi";
import { useNotification } from "../../components/notifications/NotificationProvider";
import PontoColetaForm from "./PontoColetaForm";

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
