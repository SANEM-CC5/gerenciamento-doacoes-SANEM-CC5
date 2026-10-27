"use client";
import React, { useEffect, useState } from "react";
import MenuBar from "../../../components/menubar/menubar";
import Navigation from "../../../components/navegation/navegation";
import { useRouter, useParams } from "next/navigation";
import styles from "../../pontoscoleta.module.css";
import apiService from "../../../../services/api";
import { useApi } from "../../../../hooks/useApi";
import { useNotification } from "../../../../components/notifications/NotificationProvider";
import PontoColetaForm from "../../PontoColetaForm";

export default function EditarPontoColeta() {
  const router = useRouter();
  const params = useParams();
  const id = params?.id;
  const { loading, execute } = useApi();
  const { showNotification } = useNotification();

  const [initialData, setInitialData] = useState(null);
  const [loadingData, setLoadingData] = useState(true);

  useEffect(() => {
    if (!id) return;
    (async () => {
      try {
        const point = await apiService.getCollectionPoint(id);
        setInitialData({
          name: point.name || "",
          type: point.type || "PARTNER_COLLECTION_POINT",
          street: point.street || "",
          number: point.number || "",
          neighborhood: point.neighborhood || "",
          city: point.city || "",
          state: point.state || "",
          zipCode: point.zipCode || "",
          latitude: point.latitude ?? "",
          longitude: point.longitude ?? "",
          operatingHours: point.operatingHours || "",
          phone: point.phone || "",
        });
      } catch (err) {
        showNotification(err.message || "Erro ao carregar ponto de coleta", "error");
      } finally {
        setLoadingData(false);
      }
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const handleSubmit = async (payload) => {
    try {
      await execute(() => apiService.updateCollectionPoint(id, payload));
      showNotification("Ponto de coleta atualizado com sucesso!", "success");
      setTimeout(() => router.push("/pontoscoleta/lista"), 800);
    } catch (err) {
      showNotification(err.message || "Erro ao atualizar ponto de coleta", "error");
    }
  };

  return (
    <div className={styles.containerGeral}>
      <MenuBar />
      <Navigation />
      <div className={styles.formWrapper}>
        <div className={styles.formContainer}>
          <h1 className={styles.titulo}>Editar Ponto de Coleta</h1>
          <div className={styles.decoracao}></div>
          {loadingData ? (
            <div className={styles.mapPlaceholder}>Carregando...</div>
          ) : initialData ? (
            <PontoColetaForm
              initialData={initialData}
              onSubmit={handleSubmit}
              loading={loading}
              submitLabel="Salvar Alterações"
            />
          ) : (
            <p>Ponto de coleta não encontrado.</p>
          )}
        </div>
      </div>
    </div>
  );
}
