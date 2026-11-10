"use client";
// RF20 – Cadastro, consulta, atualização e inativação de demandas prioritárias
import { useCallback, useEffect, useMemo, useState } from "react";
import MenuBar from "../components/menubar/menubar";
import Navigation from "../components/navegation/navegation";
import layout from "../estoque/estoque.module.css";
import styles from "./demandas.module.css";
import apiService from "../../services/api";
import { useNotification } from "../../components/notifications/NotificationProvider";
import ConfirmationModal from "../../components/confirmation/ConfirmationModal";
import DemandProgress from "../../components/demands/DemandProgress";
import { PRIORITY_LABELS, formatDate, todayISO } from "../../components/demands/demandUtils";
import { FaPlus, FaEdit, FaBan, FaRedo, FaCalendarAlt, FaBoxes, FaUser } from "react-icons/fa";

const EMPTY_FORM = { itemId: "", quantityNeeded: "", priority: "MEDIUM", deadline: "", justification: "" };
const MAX_JUSTIFICATION = 500;

export default function DemandasPage() {
  const { showNotification } = useNotification();

  const [demands, setDemands] = useState([]);
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [filters, setFilters] = useState({ status: "ACTIVE", priority: "", searchTerm: "" });
  const [debouncedSearch, setDebouncedSearch] = useState("");

  const [showForm, setShowForm] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(EMPTY_FORM);
  const [formErrors, setFormErrors] = useState({});
  const [saving, setSaving] = useState(false);

  const [toInactivate, setToInactivate] = useState(null);

  useEffect(() => {
    const t = setTimeout(() => setDebouncedSearch(filters.searchTerm), 400);
    return () => clearTimeout(t);
  }, [filters.searchTerm]);

  const loadDemands = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await apiService.getPriorityDemands({
        status: filters.status,
        priority: filters.priority,
        searchTerm: debouncedSearch,
      });
      setDemands(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err.message || "Erro ao carregar demandas");
      setDemands([]);
    } finally {
      setLoading(false);
    }
  }, [filters.status, filters.priority, debouncedSearch]);

  useEffect(() => { loadDemands(); }, [loadDemands]);

  useEffect(() => {
    apiService.getItems()
      .then((data) => setItems(Array.isArray(data) ? data : []))
      .catch(() => showNotification("Erro ao carregar itens do estoque", "error"));
  }, [showNotification]);

  const summary = useMemo(() => {
    const active = demands.filter((d) => d.status === "ACTIVE");
    return {
      active: active.length,
      high: active.filter((d) => d.priority === "HIGH" && !d.fulfilled).length,
      fulfilled: active.filter((d) => d.fulfilled).length,
      overdue: active.filter((d) => d.overdue).length,
    };
  }, [demands]);

  function openCreate() {
    setEditing(null);
    setForm(EMPTY_FORM);
    setFormErrors({});
    setShowForm(true);
  }

  function openEdit(demand) {
    setEditing(demand);
    setForm({
      itemId: String(demand.itemId ?? ""),
      quantityNeeded: String(demand.quantityNeeded ?? ""),
      priority: demand.priority || "MEDIUM",
      deadline: demand.deadline || "",
      justification: demand.justification || "",
    });
    setFormErrors({});
    setShowForm(true);
  }

  function closeForm() {
    setShowForm(false);
    setEditing(null);
  }

  function validate() {
    const errors = {};
    if (!form.itemId) errors.itemId = "Selecione um item do estoque.";
    const qty = Number(form.quantityNeeded);
    if (!form.quantityNeeded || !Number.isInteger(qty) || qty <= 0) {
      errors.quantityNeeded = "Informe uma quantidade inteira maior que zero.";
    }
    if (!editing && form.deadline && form.deadline < todayISO()) {
      errors.deadline = "O prazo não pode estar no passado.";
    }
    if (form.justification.length > MAX_JUSTIFICATION) {
      errors.justification = `Máximo de ${MAX_JUSTIFICATION} caracteres.`;
    }
    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    if (!validate()) return;

    const payload = {
      itemId: Number(form.itemId),
      quantityNeeded: Number(form.quantityNeeded),
      priority: form.priority,
      deadline: form.deadline || null,
      justification: form.justification.trim() || null,
    };

    setSaving(true);
    try {
      if (editing) {
        await apiService.updatePriorityDemand(editing.priorityDemandId, payload);
        showNotification("Demanda atualizada com sucesso!", "success");
      } else {
        await apiService.createPriorityDemand(payload);
        showNotification("Demanda cadastrada com sucesso!", "success");
      }
      closeForm();
      await loadDemands();
    } catch (err) {
      const fieldErrors = err.response?.errors;
      if (fieldErrors && typeof fieldErrors === "object") setFormErrors(fieldErrors);
      showNotification(err.message || "Erro ao salvar demanda", "error");
    } finally {
      setSaving(false);
    }
  }

  async function handleInactivate() {
    if (!toInactivate) return;
    try {
      await apiService.inactivatePriorityDemand(toInactivate.priorityDemandId);
      showNotification("Demanda inativada.", "success");
      setToInactivate(null);
      await loadDemands();
    } catch (err) {
      showNotification(err.message || "Erro ao inativar demanda", "error");
    }
  }

  async function handleReactivate(demand) {
    try {
      await apiService.reactivatePriorityDemand(demand.priorityDemandId);
      showNotification("Demanda reativada.", "success");
      await loadDemands();
    } catch (err) {
      showNotification(err.message || "Erro ao reativar demanda", "error");
    }
  }

  // Itens disponíveis no select: os do estoque + o item atual da demanda em edição
  const itemOptions = useMemo(() => {
    const list = [...items];
    if (editing && editing.itemId && !list.some((i) => i.itemId === editing.itemId)) {
      list.push({ itemId: editing.itemId, description: editing.itemDescription });
    }
    return list.sort((a, b) => (a.description || "").localeCompare(b.description || "", "pt-BR"));
  }, [items, editing]);

  return (
    <div className={layout.containerGeral}>
      <MenuBar />
      <Navigation />
      <div className={layout.contentWrapper}>
        <div className={layout.listContainer}>
          <div className={layout.pageHeader}>
            <h1 className={layout.titulo}>Demandas Prioritárias</h1>
            <button className={layout.addButton} onClick={openCreate} title="Nova demanda" aria-label="Nova demanda">
              <FaPlus />
            </button>
          </div>
          <div className={layout.decoracao}></div>
          <p className={styles.subtitle}>
            Itens que a SANEM mais precisa receber. O progresso é calculado automaticamente
            a partir das doações registradas desde o cadastro da demanda.
          </p>

          {filters.status !== "INACTIVE" && (
            <div className={styles.summary}>
              <div className={styles.summaryCard}>
                <span className={styles.summaryValue}>{summary.active}</span>
                <span className={styles.summaryLabel}>Ativas</span>
              </div>
              <div className={`${styles.summaryCard} ${styles.summaryHigh}`}>
                <span className={styles.summaryValue}>{summary.high}</span>
                <span className={styles.summaryLabel}>Alta prioridade pendentes</span>
              </div>
              <div className={`${styles.summaryCard} ${styles.summaryDone}`}>
                <span className={styles.summaryValue}>{summary.fulfilled}</span>
                <span className={styles.summaryLabel}>Metas atingidas</span>
              </div>
              <div className={`${styles.summaryCard} ${styles.summaryOverdue}`}>
                <span className={styles.summaryValue}>{summary.overdue}</span>
                <span className={styles.summaryLabel}>Prazo vencido</span>
              </div>
            </div>
          )}

          <div className={layout.filtersContainer}>
            <div className={`${layout.formGroup} ${styles.filterText}`}>
              <input
                type="text"
                placeholder="Buscar pelo nome do item..."
                aria-label="Buscar pelo nome do item"
                value={filters.searchTerm}
                onChange={(e) => setFilters((p) => ({ ...p, searchTerm: e.target.value }))}
              />
            </div>
            <select
              className={styles.filterSelect}
              aria-label="Filtrar por situação"
              value={filters.status}
              onChange={(e) => setFilters((p) => ({ ...p, status: e.target.value }))}
            >
              <option value="ACTIVE">Ativas</option>
              <option value="INACTIVE">Inativas</option>
              <option value="">Todas</option>
            </select>
            <select
              className={styles.filterSelect}
              aria-label="Filtrar por prioridade"
              value={filters.priority}
              onChange={(e) => setFilters((p) => ({ ...p, priority: e.target.value }))}
            >
              <option value="">Todas as prioridades</option>
              <option value="HIGH">Alta</option>
              <option value="MEDIUM">Média</option>
              <option value="LOW">Baixa</option>
            </select>
          </div>

          {loading ? (
            <p className={styles.message}>Carregando...</p>
          ) : error ? (
            <p className={`${styles.message} ${styles.error}`}>{error}</p>
          ) : demands.length === 0 ? (
            <div className={styles.empty}>
              <p>Nenhuma demanda encontrada.</p>
              {filters.status === "ACTIVE" && (
                <button type="button" className={layout.submitButton} onClick={openCreate}>
                  Cadastrar primeira demanda
                </button>
              )}
            </div>
          ) : (
            <div className={styles.grid}>
              {demands.map((d) => (
                <article
                  key={d.priorityDemandId}
                  className={`${styles.card} ${styles[`border${d.priority}`]} ${d.status === "INACTIVE" ? styles.inactive : ""}`}
                >
                  <header className={styles.cardHeader}>
                    <span className={`${styles.priorityBadge} ${styles[`priority${d.priority}`]}`}>
                      Prioridade {PRIORITY_LABELS[d.priority]}
                    </span>
                    <div className={styles.tags}>
                      {d.status === "INACTIVE" && <span className={styles.tagInactive}>Inativa</span>}
                      {d.status === "ACTIVE" && d.fulfilled && <span className={styles.tagDone}>Meta atingida</span>}
                      {d.overdue && <span className={styles.tagOverdue}>Prazo vencido</span>}
                    </div>
                  </header>

                  <h2 className={styles.itemName}>{d.itemDescription}</h2>

                  <DemandProgress demand={d} />

                  <ul className={styles.meta}>
                    <li><FaBoxes aria-hidden="true" /> Em estoque: {d.currentStock ?? "-"}</li>
                    <li>
                      <FaCalendarAlt aria-hidden="true" />{" "}
                      {d.deadline ? `Prazo: ${formatDate(d.deadline)}` : "Sem prazo definido"}
                    </li>
                    {d.createdByName && (
                      <li><FaUser aria-hidden="true" /> Cadastrada por {d.createdByName} em {formatDate(d.createdAt)}</li>
                    )}
                  </ul>

                  {d.justification && <p className={styles.justification}>{d.justification}</p>}

                  <footer className={styles.cardActions}>
                    <button type="button" className={styles.actionButton} onClick={() => openEdit(d)}>
                      <FaEdit aria-hidden="true" /> Editar
                    </button>
                    {d.status === "ACTIVE" ? (
                      <button type="button" className={`${styles.actionButton} ${styles.danger}`} onClick={() => setToInactivate(d)}>
                        <FaBan aria-hidden="true" /> Inativar
                      </button>
                    ) : (
                      <button type="button" className={`${styles.actionButton} ${styles.success}`} onClick={() => handleReactivate(d)}>
                        <FaRedo aria-hidden="true" /> Reativar
                      </button>
                    )}
                  </footer>
                </article>
              ))}
            </div>
          )}
        </div>
      </div>

      {showForm && (
        <div className={layout.modalOverlay} onClick={closeForm}>
          <div
            className={layout.modalContent}
            onClick={(e) => e.stopPropagation()}
            role="dialog"
            aria-modal="true"
            aria-labelledby="demandFormTitle"
          >
            <h2 id="demandFormTitle">{editing ? "Editar Demanda" : "Nova Demanda Prioritária"}</h2>
            <form onSubmit={handleSubmit} noValidate>
              <div className={layout.formGroup}>
                <label htmlFor="itemId">Item do estoque *</label>
                <select
                  id="itemId"
                  value={form.itemId}
                  onChange={(e) => setForm({ ...form, itemId: e.target.value })}
                  aria-invalid={!!formErrors.itemId}
                >
                  <option value="">Selecione...</option>
                  {itemOptions.map((i) => (
                    <option key={i.itemId} value={i.itemId}>
                      {i.description}{i.stockQuantity !== undefined ? ` (estoque: ${i.stockQuantity})` : ""}
                    </option>
                  ))}
                </select>
                {formErrors.itemId && <small className={styles.fieldError}>{formErrors.itemId}</small>}
                <small className={styles.fieldHelp}>
                  Item ainda não cadastrado? Cadastre-o antes em Estoque.
                </small>
              </div>

              <div className={styles.formRow}>
                <div className={layout.formGroup}>
                  <label htmlFor="quantityNeeded">Quantidade necessária *</label>
                  <input
                    id="quantityNeeded"
                    type="number"
                    min={1}
                    step={1}
                    value={form.quantityNeeded}
                    onChange={(e) => setForm({ ...form, quantityNeeded: e.target.value })}
                    aria-invalid={!!formErrors.quantityNeeded}
                  />
                  {formErrors.quantityNeeded && <small className={styles.fieldError}>{formErrors.quantityNeeded}</small>}
                </div>
                <div className={layout.formGroup}>
                  <label htmlFor="priority">Prioridade *</label>
                  <select
                    id="priority"
                    value={form.priority}
                    onChange={(e) => setForm({ ...form, priority: e.target.value })}
                  >
                    <option value="HIGH">Alta</option>
                    <option value="MEDIUM">Média</option>
                    <option value="LOW">Baixa</option>
                  </select>
                </div>
              </div>

              <div className={layout.formGroup}>
                <label htmlFor="deadline">Prazo (opcional)</label>
                <input
                  id="deadline"
                  type="date"
                  min={editing ? undefined : todayISO()}
                  value={form.deadline}
                  onChange={(e) => setForm({ ...form, deadline: e.target.value })}
                  aria-invalid={!!formErrors.deadline}
                />
                {formErrors.deadline && <small className={styles.fieldError}>{formErrors.deadline}</small>}
                <small className={styles.fieldHelp}>
                  O progresso conta as doações recebidas desde o cadastro até o fim do dia do prazo.
                </small>
              </div>

              <div className={layout.formGroup}>
                <label htmlFor="justification">Justificativa (opcional)</label>
                <textarea
                  id="justification"
                  rows={3}
                  maxLength={MAX_JUSTIFICATION}
                  className={styles.textarea}
                  placeholder="Ex.: campanha de inverno, aumento de famílias atendidas..."
                  value={form.justification}
                  onChange={(e) => setForm({ ...form, justification: e.target.value })}
                />
                <small className={styles.fieldHelp}>
                  {form.justification.length}/{MAX_JUSTIFICATION}
                </small>
                {formErrors.justification && <small className={styles.fieldError}>{formErrors.justification}</small>}
              </div>

              <div className={layout.modalActions}>
                <button type="button" className={layout.cancelButton} onClick={closeForm}>Cancelar</button>
                <button type="submit" className={layout.submitButton} disabled={saving}>
                  {saving ? "Salvando..." : editing ? "Salvar Alterações" : "Cadastrar"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <ConfirmationModal
        isOpen={!!toInactivate}
        onClose={() => setToInactivate(null)}
        onConfirm={handleInactivate}
        title="Inativar demanda"
        message={toInactivate
          ? `Inativar a demanda de "${toInactivate.itemDescription}"? Ela deixa de aparecer como ativa, mas o registro é mantido e pode ser reativado.`
          : ""}
        confirmText="Inativar"
        cancelText="Cancelar"
        type="danger"
      />
    </div>
  );
}
