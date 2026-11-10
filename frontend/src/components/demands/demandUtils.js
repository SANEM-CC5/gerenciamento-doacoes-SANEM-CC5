// RF20 – utilitários compartilhados das demandas prioritárias

export const PRIORITY_LABELS = { HIGH: "Alta", MEDIUM: "Média", LOW: "Baixa" };
export const STATUS_LABELS = { ACTIVE: "Ativa", INACTIVE: "Inativa" };

export function formatDate(isoDate) {
  if (!isoDate) return "";
  // LocalDate chega como "yyyy-MM-dd": formata sem conversão de fuso
  const [y, m, d] = String(isoDate).slice(0, 10).split("-");
  return d && m && y ? `${d}/${m}/${y}` : "";
}

export function todayISO() {
  const now = new Date();
  const local = new Date(now.getTime() - now.getTimezoneOffset() * 60000);
  return local.toISOString().slice(0, 10);
}

/** Classe do CSS module para a cor da barra de progresso. */
export function progressTone(demand) {
  if (demand.fulfilled) return "done";
  if (demand.progressPercent >= 50) return "mid";
  return "low";
}
