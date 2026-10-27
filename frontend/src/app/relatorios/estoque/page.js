"use client";

import React, { useEffect, useState } from "react";
import MenuBar from "../../components/menubar/menubar";
import Navigation from "../../components/navegation/navegation";
import RelatoriosNav from "../../components/relatoriosNav/RelatoriosNav";
import styles from "../relatorios.module.css";

import apiService from "../../../services/api";
import { mapItemFromBackend } from "../../../services/dataMapper";
import { exportTableToCsv } from "../../../utils/exportUtils";

export default function RelatorioEstoque() {
  const [loading, setLoading] = useState(true);

  const [items, setItems] = useState([]);
  const [categories, setCategories] = useState([]);
  const [totalEstoque, setTotalEstoque] = useState(0);

  // --- LÓGICA DE FILTROS (RF16) ---
  const [filters, setFilters] = useState({
    searchTerm: '',
    categoryId: '',
    size: '',
    conservationState: '',
    gender: '',
    minQuantity: '',
    maxQuantity: ''
  });
  const [debouncedSearch, setDebouncedSearch] = useState('');

  useEffect(() => {
    async function loadCategories() {
      try {
        const cats = await apiService.getCategories();
        setCategories(Array.isArray(cats) ? cats : []);
      } catch (err) {
        console.error("Erro ao carregar categorias no relatório:", err);
      }
    }
    loadCategories();
  }, []);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedSearch(filters.searchTerm);
    }, 500);
    return () => clearTimeout(handler);
  }, [filters.searchTerm]);

  useEffect(() => {
    const activeFilters = {};
    if (debouncedSearch) activeFilters.searchTerm = debouncedSearch;
    if (filters.categoryId) activeFilters.categoryId = filters.categoryId;
    if (filters.size) activeFilters.size = filters.size;
    if (filters.conservationState) activeFilters.conservationState = filters.conservationState;
    if (filters.gender) activeFilters.gender = filters.gender;
    if (filters.minQuantity) activeFilters.minQuantity = filters.minQuantity;
    if (filters.maxQuantity) activeFilters.maxQuantity = filters.maxQuantity;
    
    carregarDados(activeFilters);
  }, [
    debouncedSearch,
    filters.categoryId,
    filters.size,
    filters.conservationState,
    filters.gender,
    filters.minQuantity,
    filters.maxQuantity
  ]);

  async function carregarDados(activeFilters = {}) {
    try {
      setLoading(true);
      const response = await apiService.getItems(activeFilters);
      const itens = (response || []).map(mapItemFromBackend);
      setItems(itens);
      setTotalEstoque(
        itens.reduce((total, item) => total + (Number(item.quantidade) || 0), 0)
      );
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  }

  const handleClearFilters = () => {
    setFilters({
      searchTerm: '',
      categoryId: '',
      size: '',
      conservationState: '',
      gender: '',
      minQuantity: '',
      maxQuantity: ''
    });
    setDebouncedSearch('');
  };

  const topItens = [...items]
    .sort((a, b) => b.quantidade - a.quantidade)
    .slice(0, 10);

  const maiorQuantidade = Math.max(...topItens.map(i => i.quantidade), 1);

  return (
    <div className={styles.containerGeral}>
      <MenuBar />
      <Navigation />

      <div className={styles.pageContent}>
        <RelatoriosNav
          titulo="Relatório de Estoque"
          filters={filters}
          onExportPdf={() => window.print()}
          onExportCsv={() =>
            exportTableToCsv(
              "relatorio_estoque",
              [
                { label: "Produto", key: "nome" },
                { label: "Código", getValue: (i) => i.tagCode || "-" },
                { label: "Categoria", getValue: (i) => i.categoria || "-" },
                { label: "Tamanho", getValue: (i) => i.tamanho || "-" },
                { label: "Estado", getValue: (i) => i.estadoConservacao || "-" },
                { label: "Gênero", getValue: (i) => i.genero || "-" },
                { label: "Quantidade", key: "quantidade" },
              ],
              items
            )
          }
        />

        {/* BARRA DE FILTROS - RF16 */}
        <div className={`${styles.filtersContainer} ${styles.hideOnPrint}`}>
          <div className={`${styles.formGroup} ${styles.filterGroupText}`}>
            <input 
              type="text" 
              placeholder="Filtrar por nome ou código..." 
              value={filters.searchTerm}
              onChange={(e) => setFilters(prev => ({ ...prev, searchTerm: e.target.value }))}
            />
          </div>

          <div className={`${styles.formGroup} ${styles.filterGroupSelect}`}>
            <select
              value={filters.categoryId}
              onChange={(e) => setFilters(prev => ({ ...prev, categoryId: e.target.value }))}
            >
              <option value="">Todas as Categorias</option>
              {categories.map(cat => (
                <option key={cat.categoryId} value={cat.categoryId}>{cat.name}</option>
              ))}
            </select>
          </div>

          <div className={`${styles.formGroup} ${styles.filterGroupSelect}`}>
            <select
              value={filters.size}
              onChange={(e) => setFilters(prev => ({ ...prev, size: e.target.value }))}
            >
              <option value="">Todos os Tamanhos</option>
              <option value="PP">PP</option>
              <option value="P">P</option>
              <option value="M">M</option>
              <option value="G">G</option>
              <option value="GG">GG</option>
              <option value="XG">XG</option>
              <option value="34">34</option>
              <option value="36">36</option>
              <option value="38">38</option>
              <option value="40">40</option>
              <option value="42">42</option>
              <option value="44">44</option>
              <option value="Único">Único</option>
            </select>
          </div>

          <div className={`${styles.formGroup} ${styles.filterGroupSelect}`}>
            <select
              value={filters.conservationState}
              onChange={(e) => setFilters(prev => ({ ...prev, conservationState: e.target.value }))}
            >
              <option value="">Todos os Estados</option>
              <option value="Novo">Novo</option>
              <option value="Seminovo">Seminovo</option>
              <option value="Usado - Bom estado">Usado - Bom estado</option>
              <option value="Usado - Regular">Usado - Regular</option>
            </select>
          </div>

          <div className={`${styles.formGroup} ${styles.filterGroupSelect}`}>
            <select
              value={filters.gender}
              onChange={(e) => setFilters(prev => ({ ...prev, gender: e.target.value }))}
            >
              <option value="">Todos os Gêneros</option>
              <option value="Masculino">Masculino</option>
              <option value="Feminino">Feminino</option>
              <option value="Infantil">Infantil</option>
              <option value="Unissex">Unissex</option>
              <option value="Não aplicável">Não aplicável</option>
            </select>
          </div>

          <div className={`${styles.formGroup} ${styles.filterGroupNumber}`}>
            <input 
              type="number" 
              placeholder="Qtd Min." 
              min="0"
              value={filters.minQuantity}
              onChange={(e) => setFilters(prev => ({ ...prev, minQuantity: e.target.value }))}
            />
          </div>
          <div className={`${styles.formGroup} ${styles.filterGroupNumber}`}>
            <input 
              type="number" 
              placeholder="Qtd Máx." 
              min="0"
              value={filters.maxQuantity}
              onChange={(e) => setFilters(prev => ({ ...prev, maxQuantity: e.target.value }))}
            />
          </div>
          <button 
            className={styles.clearFilterButton} 
            onClick={handleClearFilters}
          >
            Limpar Filtros
          </button>
        </div>

        {/* Cards */}
        <div className={styles.cardsRow}>
          <div className={styles.card}>
            <div className={styles.cardInfo}>
              <span className={styles.cardValue}>{loading ? "..." : totalEstoque}</span>
              <span className={styles.cardLabel}>Itens em Estoque</span>
            </div>
          </div>

          <div className={styles.card}>
            <div className={styles.cardInfo}>
              <span className={styles.cardValue}>{loading ? "..." : items.length}</span>
              <span className={styles.cardLabel}>Produtos Cadastrados</span>
            </div>
          </div>
        </div>

        {/* Gráfico */}
        <div className={styles.chartBox}>
          <h3 className={styles.chartTitle}>Top 10 Produtos em Estoque</h3>
          <p className={styles.chartSub}>Produtos com maior quantidade disponível</p>
          {loading ? (
            <div className={styles.chartLoading}>Carregando...</div>
          ) : topItens.length === 0 ? (
            <div className={styles.chartEmpty}>Nenhum produto para exibir</div>
          ) : (
            <div style={{ marginTop: 20 }}>
              {topItens.map(item => (
                <div key={item.id} style={{ marginBottom: 14 }}>
                  <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 4, fontSize: "0.9rem" }}>
                    <span>{item.nome} {item.tamanho ? `(${item.tamanho})` : ''}</span>
                    <strong>{item.quantidade}</strong>
                  </div>
                  <div style={{ height: 18, background: "#ECEFF1", borderRadius: 8, overflow: "hidden" }}>
                    <div style={{ height: "100%", width: `${(item.quantidade / maiorQuantidade) * 100}%`, background: "#4CAF50" }} />
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Tabela */}
        <div className={styles.chartBox} style={{ marginTop: 20 }}>
          <h3 className={styles.chartTitle}>Produtos Filtrados</h3>
          <table style={{ width: "100%", borderCollapse: "collapse", marginTop: 15 }}>
            <thead>
              <tr style={{ borderBottom: "2px solid #e0e0e0" }}>
                <th align="left" style={{ padding: "10px 8px" }}>Produto</th>
                <th align="left" style={{ padding: "10px 8px" }}>Categoria</th>
                <th align="left" style={{ padding: "10px 8px" }}>Tamanho</th>
                <th align="left" style={{ padding: "10px 8px" }}>Estado</th>
                <th align="left" style={{ padding: "10px 8px" }}>Gênero</th>
                <th align="left" style={{ padding: "10px 8px" }}>Quantidade</th>
              </tr>
            </thead>
            <tbody>
              {items.length === 0 ? (
                <tr>
                  <td colSpan={6} style={{ padding: "20px", textAlign: "center", color: "#888" }}>
                    Nenhum produto encontrado com os filtros aplicados.
                  </td>
                </tr>
              ) : (
                items.map(item => (
                  <tr key={item.id} style={{ borderBottom: "1px solid #f0f0f0" }}>
                    <td style={{ padding: "10px 8px" }}>{item.nome}</td>
                    <td style={{ padding: "10px 8px" }}>{item.categoria || "-"}</td>
                    <td style={{ padding: "10px 8px" }}>{item.tamanho || "-"}</td>
                    <td style={{ padding: "10px 8px" }}>{item.estadoConservacao || "-"}</td>
                    <td style={{ padding: "10px 8px" }}>{item.genero || "-"}</td>
                    <td style={{ padding: "10px 8px", fontWeight: "600" }}>{item.quantidade}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
