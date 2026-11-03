"use client";
import MenuBar from '../components/menubar/menubar';
import Navigation from '../components/navegation/navegation';
import styles from './estoque.module.css';
import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import apiService from '../../services/api';
import { mapItemFromBackend, mapItemToBackend } from '../../services/dataMapper';
import { useApiList } from '../../hooks/useApi';
import { FaPlus, FaPrint, FaTag, FaEdit, FaTrash, FaExclamationTriangle } from 'react-icons/fa';
import { useNotification } from '../../components/notifications/NotificationProvider';
import ConfirmationModal from '../../components/confirmation/ConfirmationModal';

export default function EstoquePage() {
  const { showNotification } = useNotification();
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [itemToDelete, setItemToDelete] = useState(null);
  const emptyProduto = { nome: '', quantidade: '', tagCode: '', estoqueMinimo: '' };
  const [novoProduto, setNovoProduto] = useState(emptyProduto);
  const [editProduto, setEditProduto] = useState(emptyProduto);
  const [editingItem, setEditingItem] = useState(null);
  const [filters, setFilters] = useState({ searchTerm: '', minQuantity: '', maxQuantity: '', lowStock: false });
  const [lowStockCount, setLowStockCount] = useState(0);
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const router = useRouter();

  const {
    data: itemsRaw,
    loading,
    error,
    loadData: loadDataRaw,
    addItem,
    updateItem,
    removeItem
  } = useApiList((filters) => apiService.getItems(filters));

  const mockEstoque = Array.isArray(itemsRaw) 
    ? itemsRaw.map(mapItemFromBackend)
    : [];

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

  useEffect(() => {
    reloadItems().catch(err => {
      console.error("Erro ao carregar itens filtrados:", err);
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [debouncedSearch, filters.minQuantity, filters.maxQuantity, filters.lowStock]);

  async function handleAddProduto(e) {
    e.preventDefault();
    
    try {
      // Preparar dados diretamente no formato esperado pelo backend
      const itemData = {
        description: novoProduto.nome || '',
        stockQuantity: Number(novoProduto.quantidade) || 0,
        tagCode: novoProduto.tagCode?.trim() ? novoProduto.tagCode.trim() : null,
        minimumStock: novoProduto.estoqueMinimo === '' ? null : Number(novoProduto.estoqueMinimo)
      };
      
      await apiService.createItem(itemData);
      await reloadItems();
      
      setNovoProduto(emptyProduto);
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
      quantidade: item.quantidade || '',
      tagCode: item.tagCode || '',
      estoqueMinimo: item.estoqueMinimo ?? ''
    });
    setEditingItem(item);
    setShowEditModal(true);
  }

  async function handleEditSubmit(e) {
    e.preventDefault();
    
    if (!editingItem) return;

    try {
      const itemData = mapItemToBackend({
        nome: editProduto.nome || '',
        quantidade: Number(editProduto.quantidade) || 0,
        tagCode: editProduto.tagCode,
        estoqueMinimo: editProduto.estoqueMinimo
      });
      
      await apiService.updateItem(editingItem.id, itemData);
      await reloadItems();
      
      setEditProduto(emptyProduto);
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
              onClick={() => setShowAddModal(true)}
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

          <div className={styles.filtersContainer}>
            <div className={`${styles.formGroup} ${styles.filterGroupText}`}>
              <input 
                type="text" 
                placeholder="Filtrar por nome ou código..." 
                value={filters.searchTerm}
                onChange={(e) => setFilters(prev => ({ ...prev, searchTerm: e.target.value }))}
              />
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
            <label className={styles.lowStockFilter}>
              <input
                type="checkbox"
                checked={filters.lowStock}
                onChange={(e) => setFilters(prev => ({ ...prev, lowStock: e.target.checked }))}
              />
              Somente estoque baixo
            </label>
            <button 
              className={`${styles.cancelButton} ${styles.clearFilterButton}`} 
              onClick={() => {
                setFilters({ searchTerm: '', minQuantity: '', maxQuantity: '', lowStock: false });
                setDebouncedSearch('');
              }}
            >
              Limpar
            </button>
          </div>

          <div className={styles.tableWrapper}>
            <table className={styles.beneficiariosTable}>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nome</th>
                  <th>Quantidade</th>
                  <th>Estoque mínimo</th>
                  <th>Código/Tag</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr>
                    <td colSpan={6} className={styles.loadingMessage}>Carregando...</td>
                  </tr>
                ) : error ? (
                  <tr>
                    <td colSpan={6} className={styles.errorMessage}>{error}</td>
                  </tr>
                ) : mockEstoque.length === 0 ? (
                  <tr>
                    <td colSpan={6} className={styles.noDataMessage}>{filters.lowStock ? 'Nenhum item com estoque baixo.' : 'Nenhum produto cadastrado ainda.'}</td>
                  </tr>
                ) : (
                  mockEstoque.map(item => (
                    <tr key={item.id} className={item.estoqueBaixo ? styles.lowStockRow : undefined}>
                      <td>{item.id}</td>
                      <td>{item.nome}</td>
                      <td>
                        {item.quantidade}
                        {item.estoqueBaixo && (
                          <span className={styles.lowStockBadge} title="Saldo igual ou abaixo do estoque mínimo">
                            <FaExclamationTriangle aria-hidden="true" /> Estoque baixo
                          </span>
                        )}
                      </td>
                      <td>
                        {item.estoqueMinimo !== null && item.estoqueMinimo !== undefined ? (
                          item.estoqueMinimo
                        ) : (
                          <span style={{ color: '#999', fontStyle: 'italic' }}>Não definido</span>
                        )}
                      </td>
                      <td>
                        {item.tagCode ? (
                          <span style={{ fontWeight: '500' }}>{item.tagCode}</span>
                        ) : (
                          <span style={{ color: '#999', fontStyle: 'italic' }}>Sem tag</span>
                        )}
                      </td>
                      <td className={styles.actionButtons}>
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

      {/* Modal Adicionar */}
      {showAddModal && (
        <div className={styles.modalOverlay} onClick={() => setShowAddModal(false)}>
          <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
            <h2>Adicionar Produto</h2>
            <form onSubmit={handleAddProduto}>
              <div className={styles.formGroup}>
                <label htmlFor="nome">Nome *</label>
                <input 
                  id="nome"
                  type="text"
                  required 
                  value={novoProduto.nome} 
                  onChange={e => setNovoProduto({ ...novoProduto, nome: e.target.value })} 
                />
              </div>
              <div className={styles.formGroup}>
                <label htmlFor="tagCode">Código/Tag (Opcional)</label>
                <input 
                  id="tagCode"
                  type="text"
                  value={novoProduto.tagCode} 
                  onChange={e => setNovoProduto({ ...novoProduto, tagCode: e.target.value })} 
                  placeholder="Ex: PROD-ABC-123"
                />
              </div>
              <div className={styles.formGroup}>
                <label htmlFor="quantidade">Quantidade *</label>
                <input 
                  id="quantidade"
                  type="number" 
                  min={1} 
                  required
                  value={novoProduto.quantidade} 
                  onChange={e => setNovoProduto({ ...novoProduto, quantidade: e.target.value })} 
                />
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

      {/* Modal Editar */}
      {showEditModal && editingItem && (
        <div className={styles.modalOverlay} onClick={() => setShowEditModal(false)}>
          <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
            <h2>Editar Produto</h2>
            <form onSubmit={handleEditSubmit}>
              <div className={styles.formGroup}>
                <label htmlFor="editNome">Nome *</label>
                <input 
                  id="editNome"
                  type="text"
                  required 
                  value={editProduto.nome} 
                  onChange={e => setEditProduto({ ...editProduto, nome: e.target.value })} 
                />
              </div>
              <div className={styles.formGroup}>
                <label htmlFor="editTagCode">Código/Tag (Opcional)</label>
                <input 
                  id="editTagCode"
                  type="text"
                  value={editProduto.tagCode} 
                  onChange={e => setEditProduto({ ...editProduto, tagCode: e.target.value })} 
                  placeholder="Deixe em branco para remover o código"
                />
              </div>
              <div className={styles.formGroup}>
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
