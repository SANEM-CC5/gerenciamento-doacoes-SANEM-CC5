"use client";
import React, { useEffect, useState } from "react";
import dynamic from "next/dynamic";
import MenuBar from "../../components/menubar/menubar";
import Navigation from "../../components/navegation/navegation";
import styles from "./lista.module.css";
import { useRouter } from "next/navigation";
import apiService from "../../../services/api";
import { useApiList } from "../../../hooks/useApi";
import { FaPlus, FaEdit, FaTrash } from "react-icons/fa";
import { useNotification } from "../../../components/notifications/NotificationProvider";
import ConfirmationModal from "../../../components/confirmation/ConfirmationModal";

const CollectionPointsMap = dynamic(
  () => import("../../components/map/CollectionPointsMap"),
  {
    ssr: false,
    loading: () => <div className={styles.mapPlaceholder}>Carregando mapa...</div>,
  }
);

const TYPE_LABELS = {
  PARTNER_COLLECTION_POINT: "Ponto de coleta parceiro",
  ASSISTANCE_ENTITY_HQ: "Sede de entidade assistencial",
};

export default function ListaPontosColeta() {
  const router = useRouter();
  const { showNotification } = useNotification();

  const [confirmModal, setConfirmModal] = useState({ isOpen: false, action: null, message: "", title: "" });

  const [filters, setFilters] = useState({ name: "", type: "", neighborhood: "" });
  const [debouncedFilters, setDebouncedFilters] = useState({ name: "", type: "", neighborhood: "" });

  const {
    data: pointsRaw,
    loading,
    error,
    loadData,
  } = useApiList((apiFilters) => apiService.getCollectionPoints(apiFilters));

  const points = Array.isArray(pointsRaw) ? pointsRaw : [];

  useEffect(() => {
    const handler = setTimeout(() => setDebouncedFilters(filters), 500);
    return () => clearTimeout(handler);
  }, [filters]);

  useEffect(() => {
    const activeFilters = {};
    if (debouncedFilters.name) activeFilters.name = debouncedFilters.name;
    if (debouncedFilters.type) activeFilters.type = debouncedFilters.type;
    if (debouncedFilters.neighborhood) activeFilters.neighborhood = debouncedFilters.neighborhood;

    loadData(activeFilters).catch((err) => {
      console.error("Erro ao carregar pontos de coleta:", err);
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [debouncedFilters]);

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters((prev) => ({ ...prev, [name]: value }));
  };

  const handleClearFilters = () => {
    setFilters({ name: "", type: "", neighborhood: "" });
    setDebouncedFilters({ name: "", type: "", neighborhood: "" });
  };

  const handleEdit = (id) => {
    router.push(`/pontoscoleta/editar/${id}`);
  };

  const handleDelete = (id) => {
    setConfirmModal({
      isOpen: true,
      action: async () => {
        try {
          await apiService.deleteCollectionPoint(id);
          await loadData();
          showNotification("Ponto de coleta excluído com sucesso!", "success");
        } catch (err) {
          showNotification(err.message || "Erro ao excluir ponto de coleta", "error");
        }
      },
      message: "Tem certeza que deseja excluir este ponto de coleta?",
      title: "Confirmar Exclusão",
    });
  };

  const handleConfirm = async () => {
    if (confirmModal.action) await confirmModal.action();
    setConfirmModal({ isOpen: false, action: null, message: "", title: "" });
  };

  const getTypeClass = (type) =>
    type === "PARTNER_COLLECTION_POINT" ? styles.typePartner : styles.typeEntity;

  const buildAddress = (p) =>
    [
      [p.street, p.number].filter(Boolean).join(", "),
      p.neighborhood,
      [p.city, p.state].filter(Boolean).join(" - "),
    ]
      .filter(Boolean)
      .join(" • ");

  return (
    <div className={styles.containerGeral}>
      <MenuBar />
      <Navigation />
      <div className={styles.contentWrapper}>
        <div className={styles.listContainer}>
          <div className={styles.pageHeader}>
            <h1 className={styles.titulo}>Pontos de Coleta e Sedes</h1>
            <button
              className={styles.addButton}
              onClick={() => router.push("/pontoscoleta")}
              title="Cadastrar Novo Ponto de Coleta"
            >
              <FaPlus />
            </button>
          </div>
          <div className={styles.decoracao}></div>

          <div className={styles.mapWrapper}>
            <CollectionPointsMap points={points} />
          </div>

          <div className={styles.filtersContainer}>
            <div className={`${styles.formGroup} ${styles.filterGroupText}`}>
              <input
                type="text"
                name="name"
                placeholder="Filtrar por nome..."
                value={filters.name}
                onChange={handleFilterChange}
              />
            </div>
            <div className={`${styles.formGroup} ${styles.filterGroupText}`}>
              <select name="type" value={filters.type} onChange={handleFilterChange}>
                <option value="">Todos os tipos</option>
                <option value="PARTNER_COLLECTION_POINT">Ponto de coleta parceiro</option>
                <option value="ASSISTANCE_ENTITY_HQ">Sede de entidade assistencial</option>
              </select>
            </div>
            <div className={`${styles.formGroup} ${styles.filterGroupText}`}>
              <input
                type="text"
                name="neighborhood"
                placeholder="Bairro..."
                value={filters.neighborhood}
                onChange={handleFilterChange}
              />
            </div>
            <button
              className={`${styles.cancelButton} ${styles.clearFilterButton}`}
              onClick={handleClearFilters}
            >
              Limpar
            </button>
          </div>

          <div className={styles.tableWrapper}>
            <table className={styles.pointsTable}>
              <thead>
                <tr>
                  <th>Nome</th>
                  <th>Tipo</th>
                  <th>Endereço</th>
                  <th>Horário</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr><td colSpan={5} className={styles.loadingMessage}>Carregando...</td></tr>
                ) : error ? (
                  <tr><td colSpan={5} className={styles.errorMessage}>{error}</td></tr>
                ) : points.length === 0 ? (
                  <tr><td colSpan={5} className={styles.noDataMessage}>Nenhum ponto de coleta encontrado.</td></tr>
                ) : (
                  points.map((p) => (
                    <tr key={p.collectionPointId}>
                      <td>{p.name}</td>
                      <td><span className={getTypeClass(p.type)}>{TYPE_LABELS[p.type] || p.type}</span></td>
                      <td>{buildAddress(p) || "-"}</td>
                      <td>{p.operatingHours || "-"}</td>
                      <td className={styles.actionButtons}>
                        <button className={styles.editButton} onClick={() => handleEdit(p.collectionPointId)} disabled={loading} title="Editar"><FaEdit /></button>
                        <button className={styles.deleteButton} onClick={() => handleDelete(p.collectionPointId)} disabled={loading} title="Excluir"><FaTrash /></button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <ConfirmationModal
        isOpen={confirmModal.isOpen}
        onClose={() => setConfirmModal({ isOpen: false, action: null, message: "", title: "" })}
        onConfirm={handleConfirm}
        title={confirmModal.title}
        message={confirmModal.message}
        confirmText="Confirmar"
        cancelText="Cancelar"
        type="danger"
      />
    </div>
  );
}
