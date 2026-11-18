"use client";
import MenuBar from '../components/menubar/menubar';
import Navigation from '../components/navegation/navegation';
import styles from './estoque.module.css';
import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import apiService from '../../services/api';
import { mapItemFromBackend, mapItemToBackend } from '../../services/dataMapper';
import { useApiList } from '../../hooks/useApi';
import { FaPlus, FaPrint, FaTag, FaEdit, FaTrash, FaExclamationTriangle, FaFilter } from 'react-icons/fa';
import { useNotification } from '../../components/notifications/NotificationProvider';
import ConfirmationModal from '../../components/confirmation/ConfirmationModal';

const initialProductState = {
  nome: '',
  quantidade: '',
  tagCode: '',
  estoqueMinimo: '',
  categoriaId: '',
  tamanho: '',
  estadoConservacao: '',
  genero: '',
  type: '',
  location: 'Medianeira'
};

export default function EstoquePage() {
  const { showNotification } = useNotification();
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [itemToDelete, setItemToDelete] = useState(null);
  const [novoProduto, setNovoProduto] = useState(initialProductState);
  const [editProduto, setEditProduto] = useState(initialProductState);
  const [editingItem, setEditingItem] = useState(null);
  const [categories, setCategories] = useState([]);
  const [lowStockCount, setLowStockCount] = useState(0);
  const [showAdvancedFilters, setShowAdvancedFilters] = useState(false);

  const [filters, setFilters] = useState({
    searchTerm: '',
    minQuantity: '',
    maxQuantity: '',
    lowStock: false,
    categoryId: '',
    size: '',
    conservationState: '',
    gender: '',
    type: '',
    location: ''
  });
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const router = useRouter();

  const {
    data: itemsRaw,
    loading,
    error,
    loadData: loadDataRaw,
  } = useApiList((f) => apiService.getItems(f));

  const mockEstoque = Array.isArray(itemsRaw) 
    ? itemsRaw.map(mapItemFromBackend)
    : [];

  // Carrega categorias disponíveis
  useEffect(() => {
    async function loadCategories() {
      try {
        const cats = await apiService.getCategories();
        setCategories(Array.isArray(cats) ? cats : []);
      } catch (err) {
        console.error("Erro ao carregar categorias:", err);
      }
    }
    loadCategories();
  }, []);

  // Link vindo do sino de notificações: /estoque?estoqueBaixo=1
  useEffect(() => {
    if (typeof window === 'undefined') return;
    const params = new URLSearchParams(window.location.search);
    if (params.get('estoqueBaixo') === '1') {
      setFilters(prev => ({ ...prev, lowStock: true }));
    }
  }, []);

  // RF19: total de itens no/abaixo do estoque mínimo (independe dos filtros da tabela)
  async function refreshLowStockCount() {
    try {
      const lowItems = await apiService.getLowStockItems();
      setLowStockCount(Array.isArray(lowItems) ? lowItems.length : 0);
    } catch {
      setLowStockCount(0);
    }
  }

  function buildActiveFilters() {
    const activeFilters = {};
    if (debouncedSearch) activeFilters.searchTerm = debouncedSearch;
    if (filters.minQuantity) activeFilters.minQuantity = filters.minQuantity;
    if (filters.maxQuantity) activeFilters.maxQuantity = filters.maxQuantity;
    if (filters.lowStock) activeFilters.lowStock = true;
    if (filters.categoryId) activeFilters.categoryId = filters.categoryId;
    if (filters.size) activeFilters.size = filters.size;
    if (filters.conservationState) activeFilters.conservationState = filters.conservationState;
    if (filters.gender) activeFilters.gender = filters.gender;
    if (filters.type) activeFilters.type = filters.type;
    if (filters.location) activeFilters.location = filters.location;
    return activeFilters;
  }

  async function reloadItems() {
    await loadDataRaw(buildActiveFilters());
    refreshLowStockCount();
  }

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedSearch(filters.searchTerm);
    }, 500); 

    return () => clearTimeout(handler);
  }, [filters.searchTerm]);

  // Dispara consulta filtrada ao alterar filtros
  useEffect(() => {
    reloadItems().catch(err => {
      console.error("Erro ao carregar itens filtrados:", err);
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    debouncedSearch,
    filters.minQuantity,
    filters.maxQuantity,
    filters.lowStock,
    filters.categoryId,
    filters.size,
    filters.conservationState,
    filters.gender,
    filters.type,
    filters.location
  ]);

  function handleClearFilters() {
    setFilters({
      searchTerm: '',
      minQuantity: '',
      maxQuantity: '',
      lowStock: false,
      categoryId: '',
      size: '',
      conservationState: '',
      gender: '',
      type: '',
      location: ''
    });
    setDebouncedSearch('');
  }

  async function handleAddProduto(e) {
    e.preventDefault();
    
    try {
      const itemData = mapItemToBackend(novoProduto);
      
      await apiService.createItem(itemData);
      await reloadItems();

      setNovoProduto(initialProductState);
      setShowAddModal(false);
      showNotification("Produto adicionado com sucesso!", "success");
    } catch (err) {
      showNotification(err.message || "Erro ao adicionar produto", "error");
    }
  }

  async function handleDeleteProduto() {
    if (!itemToDelete) return;
    
    try {
      await apiService.deleteItem(itemToDelete.id);
      await reloadItems();
      setItemToDelete(null);
      showNotification("Produto excluído com sucesso!", "success");
    } catch (err) {
      showNotification(err.message || "Erro ao excluir produto", "error");
    }
  }

  function openDeleteModal(item) {
    setItemToDelete(item);
  }

  function handleEditProduto(item) {
    setEditProduto({
      nome: item.nome || '',
      quantidade: item.quantidade !== undefined && item.quantidade !== null ? String(item.quantidade) : '',
      tagCode: item.tagCode || '',
      estoqueMinimo: item.estoqueMinimo ?? '',
      categoriaId: item.categoriaId ? String(item.categoriaId) : '',
      tamanho: item.tamanho || '',
      estadoConservacao: item.estadoConservacao || '',
      genero: item.genero || '',
      type: item.type || '',
      location: item.location || ''
    });
    setEditingItem(item);
    setShowEditModal(true);
  }

  async function handleEditSubmit(e) {
    e.preventDefault();
    
    if (!editingItem) return;

    try {
      const itemData = mapItemToBackend(editProduto);
      
      await apiService.updateItem(editingItem.id, itemData);
      await reloadItems();

      setEditProduto(initialProductState);
      setShowEditModal(false);
      setEditingItem(null);
      showNotification("Produto atualizado com sucesso!", "success");
    } catch (err) {
      showNotification(err.message || "Erro ao atualizar produto", "error");
    }
  }

  async function handleGenerateLabel(item) {
    try {
      const labelData = await apiService.generateItemLabel(item.id);
      printLabel(labelData);
      showNotification("Etiqueta gerada com sucesso!", "success");
    } catch (err) {
      showNotification(err.message || "Erro ao gerar etiqueta", "error");
    }
  }

  function printLabel(labelData) {
    const printWindow = window.open('', '_blank');
    const printContent = `
      <!DOCTYPE html>
      <html>
        <head>
          <title>Etiqueta - ${labelData.description}</title>
          <style>
            @media print {
              @page { size: 4in 2in; margin: 0.2in; }
              body { margin: 0; padding: 0; }
            }
            body { 
              font-family: Arial, sans-serif; 
              padding: 10px; 
              margin: 0;
              display: flex;
              flex-direction: column;
              align-items: center;
            }
            .label-container {
              border: 2px solid #000;
              padding: 15px;
              width: 100%;
              max-width: 350px;
              text-align: center;
            }
            .label-header {
              font-size: 14px;
              font-weight: bold;
              margin-bottom: 8px;
            }
            .label-description {
              font-size: 12px;
              margin: 8px 0;
              word-wrap: break-word;
            }
            .label-code {
              font-size: 16px;
              font-weight: bold;
              margin: 8px 0;
              color: #333;
            }
            .qr-code {
              margin: 10px auto;
              display: block;
            }
          </style>
        </head>
        <body>
          <div class="label-container">
            <div class="label-header">SANEM - ETIQUETA DE ITEM</div>
            <div class="label-description">${labelData.description}</div>
            <div class="label-code">Código: ${labelData.tagCode}</div>
            <img src="data:image/png;base64,${labelData.qrCodeBase64}" alt="QR Code" class="qr-code" style="width: 120px; height: 120px;" />
          </div>
        </body>
      </html>
    `;
    printWindow.document.write(printContent);
    printWindow.document.close();
    setTimeout(() => {
      printWindow.print();
    }, 250);
  }

  return (
    <div className={styles.containerGeral}>
      <MenuBar />
      <Navigation />
      <div className={styles.contentWrapper}>
        <div className={styles.listContainer}>
          <div className={styles.pageHeader}>
            <h1 className={styles.titulo}>Controle de Estoque</h1>
            <button
              className={styles.addButton}
              data-tour="estoque-adicionar"
              onClick={() => {
                setNovoProduto(initialProductState);
                setShowAddModal(true);
              }}
              title="Adicionar Novo Produto"
            >
              <FaPlus />
            </button>
          </div>

          <div className={styles.decoracao}></div>

          {lowStockCount > 0 && (
            <div className={styles.lowStockBanner} role="status">
              <FaExclamationTriangle aria-hidden="true" />
              <span>
                {lowStockCount === 1
                  ? '1 item está no estoque mínimo ou abaixo dele.'
                  : `${lowStockCount} itens estão no estoque mínimo ou abaixo dele.`}
              </span>
              {!filters.lowStock && (
                <button
                  type="button"
                  className={styles.lowStockBannerButton}
                  onClick={() => setFilters(prev => ({ ...prev, lowStock: true }))}
                >
                  Ver itens
                </button>
              )}
            </div>
          )}

          {/* Barra de Filtros - RF16 + RF19 */}
          <div className={styles.filtersContainer} data-tour="estoque-filtros">
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

            <label className={styles.lowStockFilter} data-tour="estoque-baixo">
              <input
                type="checkbox"
                checked={filters.lowStock}
                onChange={(e) => setFilters(prev => ({ ...prev, lowStock: e.target.checked }))}
              />
              Somente estoque baixo
            </label>

            <button
              type="button"
              className={styles.advancedFiltersButton}
              onClick={() => setShowAdvancedFilters(prev => !prev)}
              aria-expanded={showAdvancedFilters}
              aria-controls="advanced-stock-filters"
            >
              <FaFilter aria-hidden="true" />
              <span>Avançado</span>
            </button>

            {showAdvancedFilters && (
              <div id="advanced-stock-filters" className={styles.advancedFiltersPanel}>
                <div className={styles.advancedFiltersGrid}>
                  <select value={filters.size} onChange={(e) => setFilters(prev => ({ ...prev, size: e.target.value }))}>
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
                  <select value={filters.conservationState} onChange={(e) => setFilters(prev => ({ ...prev, conservationState: e.target.value }))}>
                    <option value="">Todos os Estados</option>
                    <option value="Novo">Novo</option>
                    <option value="Seminovo">Seminovo</option>
                    <option value="Usado - Bom estado">Usado - Bom estado</option>
                    <option value="Usado - Regular">Usado - Regular</option>
                  </select>
                  <select value={filters.gender} onChange={(e) => setFilters(prev => ({ ...prev, gender: e.target.value }))}>
                    <option value="">Todos os Gêneros</option>
                    <option value="Masculino">Masculino</option>
                    <option value="Feminino">Feminino</option>
                    <option value="Infantil">Infantil</option>
                    <option value="Unissex">Unissex</option>
                    <option value="Não aplicável">Não aplicável</option>
                  </select>
                  <select value={filters.type} onChange={(e) => setFilters(prev => ({ ...prev, type: e.target.value }))}>
                    <option value="">Todos os Tipos</option>
                    <option value="Camisa">Camisa</option>
                    <option value="Calça">Calça</option>
                    <option value="Vestido">Vestido</option>
                    <option value="Casaco">Casaco</option>
                    <option value="Calçado">Calçado</option>
                    <option value="Acessório">Acessório</option>
                  </select>
                  <select value={filters.location} onChange={(e) => setFilters(prev => ({ ...prev, location: e.target.value }))}>
                    <option value="">Todas as Localidades</option>
                    <option value="Medianeira">Medianeira</option>
                  </select>
                  <div className={styles.formGroup}>
                    <input
                      type="number"
                      placeholder="Qtd Mín."
                      min="0"
                      value={filters.minQuantity}
                      onChange={(e) => setFilters(prev => ({ ...prev, minQuantity: e.target.value }))}
                    />
                  </div>
                  <div className={styles.formGroup}>
                    <input
                      type="number"
                      placeholder="Qtd Máx."
                      min="0"
                      value={filters.maxQuantity}
                      onChange={(e) => setFilters(prev => ({ ...prev, maxQuantity: e.target.value }))}
                    />
                  </div>
                  <button
                    type="button"
                    className={styles.clearFilterButton}
                    onClick={handleClearFilters}
                    title="Limpar todos os filtros"
                  >
                    Limpar
                  </button>
                </div>
              </div>
            )}
          </div>

          {/* Tabela de Produtos com colunas RF16 + RF19 */}
          <div className={styles.tableWrapper} data-tour="estoque-tabela">
            <table className={styles.beneficiariosTable}>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nome</th>
                  <th>Categoria</th>
                  <th>Tamanho</th>
                  <th>Estado</th>
                  <th>Gênero</th>
                  <th>Quantidade</th>
                  <th>Estoque mínimo</th>
                  <th>Código/Tag</th>
                  <th>Detalhes</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr>
                    <td colSpan={11} className={styles.loadingMessage}>Carregando...</td>
                  </tr>
                ) : error ? (
                  <tr>
                    <td colSpan={11} className={styles.errorMessage}>{error}</td>
                  </tr>
                ) : mockEstoque.length === 0 ? (
                  <tr>
                    <td colSpan={11} className={styles.noDataMessage}>
                      {filters.lowStock ? 'Nenhum item com estoque baixo.' : 'Nenhum produto encontrado com os filtros aplicados.'}
                    </td>
                  </tr>
                ) : (
                  mockEstoque.map(item => (
                    <tr key={item.id} className={item.estoqueBaixo ? styles.lowStockRow : undefined}>
                      <td data-label="ID:">{item.id}</td>
                      <td data-label="Nome:"><strong>{item.nome}</strong></td>
                      <td data-label="Categoria:">
                        {item.categoria ? (
                          <span className={styles.badgeCategory}>{item.categoria}</span>
                        ) : (
                          <span style={{ color: '#999', fontStyle: 'italic' }}>-</span>
                        )}
                      </td>
                      <td data-label="Tamanho:">{item.tamanho || '-'}</td>
                      <td data-label="Estado:">
                        {item.estadoConservacao ? (
                          <span className={styles.badgeCondition}>{item.estadoConservacao}</span>
                        ) : (
                          <span style={{ color: '#999', fontStyle: 'italic' }}>-</span>
                        )}
                      </td>
                      <td data-label="Gênero:">
                        {item.genero ? (
                          <span className={styles.badgeGender}>{item.genero}</span>
                        ) : (
                          <span style={{ color: '#999', fontStyle: 'italic' }}>-</span>
                        )}
                      </td>
                      <td data-label="Quantidade:">
                        <span style={{ fontWeight: '600' }}>{item.quantidade}</span>
                        {item.estoqueBaixo && (
                          <span className={styles.lowStockBadge} title="Saldo igual ou abaixo do estoque mínimo">
                            <FaExclamationTriangle aria-hidden="true" /> Estoque baixo
                          </span>
                        )}
                      </td>
                      <td data-label="Estoque mínimo:">
                        {item.estoqueMinimo !== null && item.estoqueMinimo !== undefined ? (
                          item.estoqueMinimo
                        ) : (
                          <span style={{ color: '#999', fontStyle: 'italic' }}>Não definido</span>
                        )}
                      </td>
                      <td data-label="Código/Tag:">
                        {item.tagCode ? (
                          <span style={{ fontWeight: '500' }}>{item.tagCode}</span>
                        ) : (
                          <span style={{ color: '#999', fontStyle: 'italic' }}>Sem tag</span>
                        )}
                      </td>
                      <td data-label="Detalhes:">
                        {[item.type, item.location].filter(Boolean).join(' | ') || '-'}
                      </td>
                      <td className={styles.actionButtons} data-tour="estoque-acoes">
                        <button
                          className={styles.editButton}
                          onClick={() => handleEditProduto(item)}
                          disabled={loading}
                          title="Editar"
                        >
                          <FaEdit />
                        </button>
                        <button
                          className={styles.editButton}
                          onClick={() => handleGenerateLabel(item)}
                          disabled={loading}
                          title="Gerar Etiqueta"
                          style={{ background: '#2196F3', marginLeft: '5px' }}
                        >
                          <FaTag />
                        </button>
                        <button
                          className={styles.deleteButton}
                          onClick={() => openDeleteModal(item)}
                          disabled={loading}
                          title="Excluir"
                        >
                          <FaTrash />
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* Modal Adicionar Produto */}
      {showAddModal && (
        <div className={styles.modalOverlay} onClick={() => setShowAddModal(false)}>
          <div className={styles.modalContent} onClick={(e) => e.stopPropagation()} style={{ maxWidth: '600px' }}>
            <h2>Adicionar Produto</h2>
            <form onSubmit={handleAddProduto}>
              <div className={styles.formGroup}>
                <label htmlFor="nome">Nome / Descrição *</label>
                <input
                  id="nome"
                  type="text"
                  required 
                  value={novoProduto.nome} 
                  onChange={e => setNovoProduto({ ...novoProduto, nome: e.target.value })} 
                  placeholder="Ex: Camiseta Básica, Tênis, Arroz..."
                />
              </div>

              <div className={styles.formRow}>
                <div className={styles.formGroup} style={{ flex: 1 }}>
                  <label htmlFor="categoriaId">Categoria</label>
                  <select
                    id="categoriaId"
                    value={novoProduto.categoriaId}
                    onChange={e => setNovoProduto({ ...novoProduto, categoriaId: e.target.value })}
                  >
                    <option value="">Selecione uma categoria...</option>
                    {categories.map(cat => (
                      <option key={cat.categoryId} value={cat.categoryId}>{cat.name}</option>
                    ))}
                  </select>
                </div>

                <div className={styles.formGroup} style={{ flex: 1 }}>
                  <label htmlFor="tamanho">Tamanho</label>
                  <input 
                    id="tamanho"
                    type="text"
                    value={novoProduto.tamanho} 
                    onChange={e => setNovoProduto({ ...novoProduto, tamanho: e.target.value })} 
                    placeholder="Ex: P, M, G, 40, Único"
                    list="tamanhosList"
                  />
                  <datalist id="tamanhosList">
                    <option value="PP" />
                    <option value="P" />
                    <option value="M" />
                    <option value="G" />
                    <option value="GG" />
                    <option value="XG" />
                    <option value="34" />
                    <option value="36" />
                    <option value="38" />
                    <option value="40" />
                    <option value="42" />
                    <option value="44" />
                    <option value="Único" />
                  </datalist>
                </div>
              </div>

              <div className={styles.formRow}>
                <div className={styles.formGroup} style={{ flex: 1 }}>
                  <label htmlFor="estadoConservacao">Estado de Conservação</label>
                  <select
                    id="estadoConservacao"
                    value={novoProduto.estadoConservacao}
                    onChange={e => setNovoProduto({ ...novoProduto, estadoConservacao: e.target.value })}
                  >
                    <option value="">Selecione o estado...</option>
                    <option value="Novo">Novo</option>
                    <option value="Seminovo">Seminovo</option>
                    <option value="Usado - Bom estado">Usado - Bom estado</option>
                    <option value="Usado - Regular">Usado - Regular</option>
                  </select>
                </div>

                <div className={styles.formGroup} style={{ flex: 1 }}>
                  <label htmlFor="genero">Gênero / Destinação</label>
                  <select
                    id="genero"
                    value={novoProduto.genero}
                    onChange={e => setNovoProduto({ ...novoProduto, genero: e.target.value })}
                  >
                    <option value="">Selecione...</option>
                    <option value="Masculino">Masculino</option>
                    <option value="Feminino">Feminino</option>
                    <option value="Infantil">Infantil</option>
                    <option value="Unissex">Unissex</option>
                    <option value="Não aplicável">Não aplicável</option>
                  </select>
                </div>
              </div>

              <div className={styles.formRow}>
                <div className={styles.formGroup} style={{ flex: 1 }}>
                  <label htmlFor="type">Tipo (Opcional)</label>
                  <input id="type" type="text" value={novoProduto.type} onChange={e => setNovoProduto({ ...novoProduto, type: e.target.value })} placeholder="Ex: Camisa, Calça..." />
                </div>
                <div className={styles.formGroup} style={{ flex: 1 }}>
                  <label htmlFor="location">Localização (Opcional)</label>
                  <input id="location" type="text" value={novoProduto.location} onChange={e => setNovoProduto({ ...novoProduto, location: e.target.value })} />
                </div>
              </div>

              <div className={styles.formRow}>
                <div className={styles.formGroup} style={{ flex: 1 }}>
                  <label htmlFor="tagCode">Código/Tag (Opcional)</label>
                  <input 
                    id="tagCode"
                    type="text"
                    value={novoProduto.tagCode} 
                    onChange={e => setNovoProduto({ ...novoProduto, tagCode: e.target.value })} 
                    placeholder="Ex: PROD-ABC-123"
                  />
                </div>

                <div className={styles.formGroup} style={{ flex: 1 }}>
                  <label htmlFor="quantidade">Quantidade *</label>
                  <input 
                    id="quantidade"
                    type="number" 
                    min={0} 
                    required
                    value={novoProduto.quantidade} 
                    onChange={e => setNovoProduto({ ...novoProduto, quantidade: e.target.value })} 
                  />
                </div>
              </div>

              <div className={styles.formGroup}>
                <label htmlFor="estoqueMinimo">Estoque mínimo (opcional)</label>
                <input
                  id="estoqueMinimo"
                  type="number"
                  min={0}
                  value={novoProduto.estoqueMinimo}
                  onChange={e => setNovoProduto({ ...novoProduto, estoqueMinimo: e.target.value })}
                  placeholder="Ex: 5 — avisa quando o saldo chegar a esse valor"
                  aria-describedby="estoqueMinimoHelp"
                />
                <small id="estoqueMinimoHelp" className={styles.fieldHelp}>
                  Administradores e atendentes serão notificados quando a quantidade atingir ou ficar abaixo deste valor. Deixe em branco para não monitorar.
                </small>
              </div>
              <div className={styles.modalActions}>
                <button 
                  type="button" 
                  className={styles.cancelButton} 
                  onClick={() => setShowAddModal(false)}
                >
                  Cancelar
                </button>
                <button 
                  type="submit" 
                  className={styles.submitButton}
                  disabled={loading}
                >
                  {loading ? "Adicionando..." : "Adicionar"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Editar Produto */}
      {showEditModal && editingItem && (
        <div className={styles.modalOverlay} onClick={() => setShowEditModal(false)}>
          <div className={styles.modalContent} onClick={(e) => e.stopPropagation()} style={{ maxWidth: '600px' }}>
            <h2>Editar Produto</h2>
            <form onSubmit={handleEditSubmit}>
              <div className={styles.formGroup}>
                <label htmlFor="editNome">Nome / Descrição *</label>
                <input
                  id="editNome"
                  type="text"
                  required 
                  value={editProduto.nome} 
                  onChange={e => setEditProduto({ ...editProduto, nome: e.target.value })} 
                />
              </div>

              <div className={styles.formRow}>
                <div className={styles.formGroup} style={{ flex: 1 }}>
                  <label htmlFor="editCategoriaId">Categoria</label>
                  <select
                    id="editCategoriaId"
                    value={editProduto.categoriaId}
                    onChange={e => setEditProduto({ ...editProduto, categoriaId: e.target.value })}
                  >
                    <option value="">Selecione uma categoria...</option>
                    {categories.map(cat => (
                      <option key={cat.categoryId} value={cat.categoryId}>{cat.name}</option>
                    ))}
                  </select>
                </div>

                <div className={styles.formGroup} style={{ flex: 1 }}>
                  <label htmlFor="editTamanho">Tamanho</label>
                  <input 
                    id="editTamanho"
                    type="text"
                    value={editProduto.tamanho} 
                    onChange={e => setEditProduto({ ...editProduto, tamanho: e.target.value })} 
                    placeholder="Ex: P, M, G, 40, Único"
                    list="editTamanhosList"
                  />
                  <datalist id="editTamanhosList">
                    <option value="PP" />
                    <option value="P" />
                    <option value="M" />
                    <option value="G" />
                    <option value="GG" />
                    <option value="XG" />
                    <option value="34" />
                    <option value="36" />
                    <option value="38" />
                    <option value="40" />
                    <option value="42" />
                    <option value="44" />
                    <option value="Único" />
                  </datalist>
                </div>
              </div>

              <div className={styles.formRow}>
                <div className={styles.formGroup} style={{ flex: 1 }}>
                  <label htmlFor="editEstadoConservacao">Estado de Conservação</label>
                  <select
                    id="editEstadoConservacao"
                    value={editProduto.estadoConservacao}
                    onChange={e => setEditProduto({ ...editProduto, estadoConservacao: e.target.value })}
                  >
                    <option value="">Selecione o estado...</option>
                    <option value="Novo">Novo</option>
                    <option value="Seminovo">Seminovo</option>
                    <option value="Usado - Bom estado">Usado - Bom estado</option>
                    <option value="Usado - Regular">Usado - Regular</option>
                  </select>
                </div>

                <div className={styles.formGroup} style={{ flex: 1 }}>
                  <label htmlFor="editGenero">Gênero / Destinação</label>
                  <select
                    id="editGenero"
                    value={editProduto.genero}
                    onChange={e => setEditProduto({ ...editProduto, genero: e.target.value })}
                  >
                    <option value="">Selecione...</option>
                    <option value="Masculino">Masculino</option>
                    <option value="Feminino">Feminino</option>
                    <option value="Infantil">Infantil</option>
                    <option value="Unissex">Unissex</option>
                    <option value="Não aplicável">Não aplicável</option>
                  </select>
                </div>
              </div>

              <div className={styles.formRow}>
                <div className={styles.formGroup} style={{ flex: 1 }}>
                  <label htmlFor="editType">Tipo (Opcional)</label>
                  <input id="editType" type="text" value={editProduto.type} onChange={e => setEditProduto({ ...editProduto, type: e.target.value })} placeholder="Ex: Camisa, Calça..." />
                </div>
                <div className={styles.formGroup} style={{ flex: 1 }}>
                  <label htmlFor="editLocation">Localização (Opcional)</label>
                  <input id="editLocation" type="text" value={editProduto.location} onChange={e => setEditProduto({ ...editProduto, location: e.target.value })} />
                </div>
              </div>

              <div className={styles.formRow}>
                <div className={styles.formGroup} style={{ flex: 1 }}>
                  <label htmlFor="editTagCode">Código/Tag (Opcional)</label>
                  <input 
                    id="editTagCode"
                    type="text"
                    value={editProduto.tagCode} 
                    onChange={e => setEditProduto({ ...editProduto, tagCode: e.target.value })} 
                    placeholder="Deixe em branco para remover o código"
                  />
                </div>

                <div className={styles.formGroup} style={{ flex: 1 }}>
                  <label htmlFor="editQuantidade">Quantidade *</label>
                  <input 
                    id="editQuantidade"
                    type="number" 
                    min={0} 
                    required
                    value={editProduto.quantidade} 
                    onChange={e => setEditProduto({ ...editProduto, quantidade: e.target.value })} 
                  />
                </div>
              </div>

              <div className={styles.formGroup}>
                <label htmlFor="editEstoqueMinimo">Estoque mínimo (opcional)</label>
                <input
                  id="editEstoqueMinimo"
                  type="number"
                  min={0}
                  value={editProduto.estoqueMinimo}
                  onChange={e => setEditProduto({ ...editProduto, estoqueMinimo: e.target.value })}
                  placeholder="Ex: 5 — avisa quando o saldo chegar a esse valor"
                  aria-describedby="editEstoqueMinimoHelp"
                />
                <small id="editEstoqueMinimoHelp" className={styles.fieldHelp}>
                  Administradores e atendentes serão notificados quando a quantidade atingir ou ficar abaixo deste valor. Deixe em branco para não monitorar.
                </small>
              </div>
              <div className={styles.modalActions}>
                <button 
                  type="button" 
                  className={styles.cancelButton} 
                  onClick={() => {
                    setShowEditModal(false);
                    setEditingItem(null);
                  }}
                >
                  Cancelar
                </button>
                <button 
                  type="submit" 
                  className={styles.submitButton}
                  disabled={loading}
                >
                  {loading ? "Salvando..." : "Salvar Alterações"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <ConfirmationModal
        isOpen={!!itemToDelete}
        onClose={() => setItemToDelete(null)}
        onConfirm={handleDeleteProduto}
        title="Confirmar Exclusão"
        message={itemToDelete ? `Tem certeza que deseja excluir o produto "${itemToDelete.nome}"?` : ""}
        confirmText="Excluir"
        cancelText="Cancelar"
        type="danger"
      />
    </div>
  );
}
