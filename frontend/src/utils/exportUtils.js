// Utilitário para exportação de dados em CSV e PDF no Frontend

/**
 * Converte dados de tabela diretamente em CSV e dispara o download no navegador.
 * Usa charset UTF-8 com BOM (\uFEFF) para garantir abertura correta de acentos no Excel.
 */
export function exportTableToCsv(filename, columns, rows) {
  if (!rows || rows.length === 0) {
    alert("Não há dados para exportar.");
    return;
  }

  // BOM UTF-8
  let csvContent = "\uFEFF";

  // Linha de cabeçalho
  const headerLine = columns.map(col => escapeCsvValue(col.label || col.header || col)).join(";");
  csvContent += headerLine + "\r\n";

  // Linhas de dados
  rows.forEach(row => {
    const rowValues = columns.map(col => {
      const key = col.key || col.id;
      let val = "";
      if (typeof col.getValue === "function") {
        val = col.getValue(row);
      } else if (key) {
        val = row[key];
      }
      return escapeCsvValue(val);
    });
    csvContent += rowValues.join(";") + "\r\n";
  });

  // Criar Blob e disparar download
  const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.setAttribute("href", url);
  link.setAttribute("download", `${filename}_${getFormattedDate()}.csv`);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}

function escapeCsvValue(val) {
  if (val === null || val === undefined) return "";
  const str = String(val).trim();
  if (str.includes(";") || str.includes('"') || str.includes("\n") || str.includes("\r")) {
    return `"${str.replace(/"/g, '""')}"`;
  }
  return str;
}

function getFormattedDate() {
  const d = new Date();
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}
