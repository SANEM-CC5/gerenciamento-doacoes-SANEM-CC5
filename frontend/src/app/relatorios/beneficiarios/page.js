"use client";

import React, { useEffect, useState } from "react";
import MenuBar from "../../components/menubar/menubar";
import Navigation from "../../components/navegation/navegation";
import RelatoriosNav from "../../components/relatoriosNav/RelatoriosNav";
import styles from "../relatorios.module.css";

import apiService from "../../../services/api";
import { mapBeneficiaryFromBackend } from "../../../services/dataMapper";
import { exportTableToCsv } from "../../../utils/exportUtils";

export default function RelatorioBeneficiarios() {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [beneficiarios, setBeneficiarios] = useState([]);

  const [aprovados, setAprovados] = useState(0);
  const [pendentes, setPendentes] = useState(0);
  const [rejeitados, setRejeitados] = useState(0);

  const [filters, setFilters] = useState({
    startDate: "",
    endDate: "",
    searchTerm: "",
    beneficiaryStatus: "",
  });

  useEffect(() => {
    const timer = setTimeout(() => {
      carregarDados(filters);
    }, 500);

    return () => clearTimeout(timer);
  }, [filters]);

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleClearFilters = () => {
    setFilters({
      startDate: "",
      endDate: "",
      searchTerm: "",
      beneficiaryStatus: "",
    });
  };

  async function carregarDados(activeFilters = filters) {
    if (activeFilters.startDate && activeFilters.endDate) {
      if (new Date(activeFilters.startDate) > new Date(activeFilters.endDate)) {
        setError("A data inicial não pode ser maior que a data final.");
        setBeneficiarios([]);
        setAprovados(0);
        setPendentes(0);
        setRejeitados(0);
        setLoading(false);
        return;
      }
    }

    try {
      setLoading(true);
      setError(null);

      const response = await apiService.getBeneficiaries();
      let lista = (response || []).map(mapBeneficiaryFromBackend);

      if (activeFilters.searchTerm) {
        const term = activeFilters.searchTerm.toLowerCase();
        lista = lista.filter(
          (b) =>
            (b.nomeCompleto || "").toLowerCase().includes(term) ||
            (b.cpfCrnm || "").toLowerCase().includes(term)
        );
      }

      if (activeFilters.beneficiaryStatus) {
        lista = lista.filter(
          (b) => b.status === activeFilters.beneficiaryStatus
        );
      }

      if (activeFilters.startDate || activeFilters.endDate) {
        const dateQuery = {};
        if (activeFilters.startDate) dateQuery.startDate = activeFilters.startDate;
        if (activeFilters.endDate) dateQuery.endDate = activeFilters.endDate;

        const [withdrawalsRes, cardsRes] = await Promise.allSettled([
          apiService.getWithdrawals(dateQuery),
          apiService.getCards(),
        ]);

        const withdrawals =
          withdrawalsRes.status === "fulfilled" && Array.isArray(withdrawalsRes.value)
            ? withdrawalsRes.value
            : [];
        const cards =
          cardsRes.status === "fulfilled" && Array.isArray(cardsRes.value)
            ? cardsRes.value
            : [];

        const withdrawalsCountByBeneficiary = {};
        const activeBeneficiaryIds = new Set();

        withdrawals.forEach((w) => {
          const benId = w.beneficiary?.beneficiaryId ?? w.beneficiary?.id;
          if (benId != null) {
            const idStr = String(benId);
            activeBeneficiaryIds.add(idStr);
            const itemsCount = (w.items || []).reduce(
              (acc, it) => acc + (Number(it.quantity) || 0),
              0
            );
            withdrawalsCountByBeneficiary[idStr] =
              (withdrawalsCountByBeneficiary[idStr] || 0) + itemsCount;
          }
        });

        cards.forEach((c) => {
          const issueDateStr = c.issueDate ? String(c.issueDate).slice(0, 10) : null;
          if (!issueDateStr) return;
          const afterStart =
            !activeFilters.startDate || issueDateStr >= activeFilters.startDate;
          const beforeEnd =
            !activeFilters.endDate || issueDateStr <= activeFilters.endDate;
          if (afterStart && beforeEnd) {
            const benId = c.beneficiary?.beneficiaryId ?? c.beneficiaryId;
            if (benId != null) {
              activeBeneficiaryIds.add(String(benId));
            }
          }
        });

        lista = lista
          .filter((b) => activeBeneficiaryIds.has(String(b.id)))
          .map((b) => {
            const periodUsed = withdrawalsCountByBeneficiary[String(b.id)];
            if (periodUsed !== undefined) {
              const limit = b.withdrawalLimit;
              return {
                ...b,
                currentWithdrawalsThisMonth: periodUsed,
                remainingWithdrawals:
                  limit != null ? Math.max(limit - periodUsed, 0) : b.remainingWithdrawals,
              };
            }
            return b;
          });
      }

      setBeneficiarios(lista);

      setAprovados(lista.filter((b) => b.status === "APPROVED").length);
      setPendentes(lista.filter((b) => b.status === "PENDING").length);
      setRejeitados(lista.filter((b) => b.status === "REJECTED").length);
    } catch (err) {
      console.error(err);
      setError("Erro ao carregar os dados de beneficiários.");
    } finally {
      setLoading(false);
    }
  }

  const ranking = [...beneficiarios]
    .sort(
      (a, b) =>
        (b.currentWithdrawalsThisMonth || 0) -
        (a.currentWithdrawalsThisMonth || 0)
    )
    .slice(0, 10);

  const maiorValor = Math.max(
    ...ranking.map((b) => b.currentWithdrawalsThisMonth || 0),
    1
  );

  function traduzirStatus(status) {
    switch (status) {
      case "APPROVED":
        return "Aprovado";

      case "PENDING":
        return "Pendente";

      case "REJECTED":
        return "Rejeitado";

      default:
        return status;
    }
  }

  function corStatus(status) {
    switch (status) {
      case "APPROVED":
        return "#4CAF50";

      case "PENDING":
        return "#FFA726";

      case "REJECTED":
        return "#EF5350";

      default:
        return "#90A4AE";
    }
  }

  return (
    <div className={styles.containerGeral}>
      <MenuBar />
      <Navigation />

      <div className={styles.pageContent}>
        <RelatoriosNav
          titulo="Relatório de Beneficiários"
          filters={{
            startDate: filters.startDate,
            endDate: filters.endDate,
            fullName: filters.searchTerm,
            beneficiaryStatus: filters.beneficiaryStatus,
          }}
          onExportPdf={() => window.print()}
          onExportCsv={() =>
            exportTableToCsv(
              "relatorio_beneficiarios",
              [
                { label: "Nome", key: "nomeCompleto" },
                { label: "CPF", key: "cpfCrnm" },
                { label: "Telefone", key: "telefoneCelular" },
                { label: "Status", getValue: (b) => traduzirStatus(b.status) },
                { label: "Limite", getValue: (b) => b.withdrawalLimit ?? "-" },
                {
                  label: "Utilizado",
                  getValue: (b) => b.currentWithdrawalsThisMonth ?? 0,
                },
                {
                  label: "Restante",
                  getValue: (b) => b.remainingWithdrawals ?? 0,
                },
              ],
              beneficiarios
            )
          }
        />

        {/* Barra de Filtros Padronizada */}
        <div className={`${styles.filtersContainer} ${styles.hideOnPrint}`}>
          <div className={`${styles.formGroup} ${styles.filterGroupDate}`}>
            <input
              type="date"
              name="startDate"
              value={filters.startDate}
              onChange={handleFilterChange}
              className={styles.filterInput}
              title="Data Inicial"
            />
          </div>
          <span className={styles.dateSeparator}>até</span>
          <div className={`${styles.formGroup} ${styles.filterGroupDate}`}>
            <input
              type="date"
              name="endDate"
              value={filters.endDate}
              onChange={handleFilterChange}
              className={styles.filterInput}
              title="Data Final"
            />
          </div>
          <div className={styles.formGroup}>
            <input
              type="text"
              name="searchTerm"
              placeholder="Filtrar por nome ou CPF..."
              value={filters.searchTerm}
              onChange={handleFilterChange}
              className={styles.filterInput}
            />
          </div>
          <div className={styles.formGroup}>
            <select
              name="beneficiaryStatus"
              value={filters.beneficiaryStatus}
              onChange={handleFilterChange}
              className={styles.filterSelect}
            >
              <option value="">Todos os Status</option>
              <option value="APPROVED">Aprovado</option>
              <option value="PENDING">Pendente</option>
              <option value="REJECTED">Rejeitado</option>
            </select>
          </div>
          <button
            className={styles.clearFilterButton}
            onClick={handleClearFilters}
          >
            Limpar Filtros
          </button>
        </div>

        {error && <div className={styles.errorText}>{error}</div>}

        {/* Cards */}

        <div className={styles.cardsRow}>
          <div className={styles.card}>
            <div className={styles.cardInfo}>
              <span className={styles.cardValue}>
                {loading ? "..." : beneficiarios.length}
              </span>

              <span className={styles.cardLabel}>Total de Beneficiários</span>
            </div>
          </div>

          <div className={styles.card}>
            <div className={styles.cardInfo}>
              <span className={styles.cardValue}>
                {loading ? "..." : aprovados}
              </span>

              <span className={styles.cardLabel}>Aprovados</span>
            </div>
          </div>

          <div className={styles.card}>
            <div className={styles.cardInfo}>
              <span className={styles.cardValue}>
                {loading ? "..." : pendentes}
              </span>

              <span className={styles.cardLabel}>Pendentes</span>
            </div>
          </div>

          <div className={styles.card}>
            <div className={styles.cardInfo}>
              <span className={styles.cardValue}>
                {loading ? "..." : rejeitados}
              </span>

              <span className={styles.cardLabel}>Rejeitados</span>
            </div>
          </div>
        </div>

        {/* Gráfico */}

        <div className={styles.chartBox}>
          <h3 className={styles.chartTitle}>Beneficiários com Mais Retiradas</h3>

          <p className={styles.chartSub}>
            {filters.startDate || filters.endDate
              ? "Retiradas realizadas no período selecionado"
              : "Retiradas realizadas no mês atual"}
          </p>

          {loading ? (
            <div className={styles.chartLoading}>Carregando...</div>
          ) : ranking.length === 0 ? (
            <div className={styles.chartEmpty}>
              Nenhum beneficiário encontrado para os filtros aplicados
            </div>
          ) : (
            <div style={{ marginTop: 20 }}>
              {ranking.map((beneficiario) => (
                <div
                  key={beneficiario.id}
                  style={{
                    marginBottom: 14,
                  }}
                >
                  <div
                    style={{
                      display: "flex",
                      justifyContent: "space-between",
                      marginBottom: 4,
                    }}
                  >
                    <span>{beneficiario.nomeCompleto}</span>

                    <strong>{beneficiario.currentWithdrawalsThisMonth}</strong>
                  </div>

                  <div
                    style={{
                      height: 18,
                      background: "#ECEFF1",
                      borderRadius: 8,
                      overflow: "hidden",
                    }}
                  >
                    <div
                      style={{
                        height: "100%",
                        width: `${
                          ((beneficiario.currentWithdrawalsThisMonth || 0) /
                            maiorValor) *
                          100
                        }%`,
                        background: "#AB47BC",
                      }}
                    />
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Tabela */}

        <div className={styles.chartBox} style={{ marginTop: 20 }}>
          <h3 className={styles.chartTitle}>Situação dos Beneficiários</h3>

          <table
            style={{
              width: "100%",
              borderCollapse: "collapse",
              marginTop: 15,
            }}
          >
            <thead>
              <tr>
                <th align="left">Nome</th>

                <th align="left">Status</th>

                <th align="left">Limite</th>

                <th align="left">Utilizado</th>

                <th align="left">Restante</th>
              </tr>
            </thead>

            <tbody>
              {beneficiarios.map((beneficiario) => (
                <tr key={beneficiario.id}>
                  <td>{beneficiario.nomeCompleto}</td>

                  <td>
                    <span
                      style={{
                        color: corStatus(beneficiario.status),
                        fontWeight: 600,
                      }}
                    >
                      {traduzirStatus(beneficiario.status)}
                    </span>
                  </td>

                  <td>{beneficiario.withdrawalLimit ?? "-"}</td>

                  <td>{beneficiario.currentWithdrawalsThisMonth ?? 0}</td>

                  <td>{beneficiario.remainingWithdrawals ?? 0}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
