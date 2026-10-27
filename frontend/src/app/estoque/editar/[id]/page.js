"use client";
import React, { useState, useEffect } from "react";
import MenuBar from "../../../components/menubar/menubar";
import Navigation from "../../../components/navegation/navegation";
import { useRouter, useParams } from "next/navigation";
import styles from "../../estoque.module.css";
import apiService from "../../../../services/api";
import { mapItemFromBackend, mapItemToBackend } from "../../../../services/dataMapper";
import { useApi } from "../../../../hooks/useApi";
import { useNotification } from "../../../../components/notifications/NotificationProvider";

const EditarItem = () => {
  const router = useRouter();
  const params = useParams();
  const id = params.id;
  const { loading, error, execute, clearError } = useApi();
  const { showNotification } = useNotification();
  const [loadingData, setLoadingData] = useState(true);
  const [categories, setCategories] = useState([]);
  
  const [form, setForm] = useState({
    nome: "",
    quantidade: "",
    tagCode: "",
    categoriaId: "",
    tamanho: "",
    estadoConservacao: "",
    genero: ""
  });

  useEffect(() => {
    const loadCategories = async () => {
      try {
        const cats = await apiService.getCategories();
        setCategories(Array.isArray(cats) ? cats : []);
      } catch (err) {
        console.error("Erro ao carregar categorias:", err);
      }
    };
    loadCategories();
  }, []);

  useEffect(() => {
    const loadItem = async () => {
      try {
        setLoadingData(true);
        const item = await apiService.getItem(id);
        const mappedItem = mapItemFromBackend(item);
        setForm({
          nome: mappedItem.nome || "",
          quantidade: mappedItem.quantidade !== undefined ? String(mappedItem.quantidade) : "",
          tagCode: mappedItem.tagCode || "",
          categoriaId: mappedItem.categoriaId ? String(mappedItem.categoriaId) : "",
          tamanho: mappedItem.tamanho || "",
          estadoConservacao: mappedItem.estadoConservacao || "",
          genero: mappedItem.genero || ""
        });
      } catch (err) {
        console.error("Erro ao carregar item:", err);
        showNotification(err.message || "Erro ao carregar item", "error");
        setTimeout(() => router.push("/estoque"), 2000);
      } finally {
        setLoadingData(false);
      }
    };

    if (id) {
      loadItem();
    }
  }, [id, router, showNotification]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  async function handleSubmit(e) {
    e.preventDefault();
    clearError();

    try {
      const itemData = mapItemToBackend({
        ...form,
        quantidade: Number(form.quantidade)
      });

      await execute(() => apiService.updateItem(id, itemData));
      
      showNotification("Item atualizado com sucesso!", "success");
      setTimeout(() => router.push("/estoque"), 1000);
    } catch (err) {
      showNotification(err.message || "Erro ao atualizar item", "error");
    }
  }

  if (loadingData) {
    return (
      <div className={styles.containerGeral}>
        <MenuBar />
        <Navigation />
        <div className={styles.contentWrapper}>
          <div className={styles.listContainer}>
            <h1 className={styles.titulo}>Carregando...</h1>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.containerGeral}>
      <MenuBar />
      <Navigation />
      <div className={styles.contentWrapper}>
        <div className={styles.listContainer}>
          <h1 className={styles.titulo}>Editar Item do Estoque</h1>
          <div className={styles.decoracao}></div>
          <form onSubmit={handleSubmit} className={styles.formulario} style={{ maxWidth: '600px', margin: '0 auto' }}>
            <div className={styles.formGroup}>
              <label htmlFor="nome"><b>Nome / Descrição *</b></label>
              <input 
                id="nome" 
                name="nome" 
                className={styles.formInput}
                value={form.nome} 
                onChange={handleChange} 
                required 
                placeholder="Nome do item" 
              />
            </div>

            <div className={styles.formRow}>
              <div className={styles.formGroup} style={{ flex: 1 }}>
                <label htmlFor="categoriaId"><b>Categoria</b></label>
                <select
                  id="categoriaId"
                  name="categoriaId"
                  value={form.categoriaId}
                  onChange={handleChange}
                  className={styles.formInput}
                >
                  <option value="">Selecione uma categoria...</option>
                  {categories.map(cat => (
                    <option key={cat.categoryId} value={cat.categoryId}>{cat.name}</option>
                  ))}
                </select>
              </div>

              <div className={styles.formGroup} style={{ flex: 1 }}>
                <label htmlFor="tamanho"><b>Tamanho</b></label>
                <input 
                  id="tamanho"
                  name="tamanho"
                  type="text"
                  className={styles.formInput}
                  value={form.tamanho} 
                  onChange={handleChange} 
                  placeholder="Ex: P, M, G, 40, Único"
                  list="editPageTamanhosList"
                />
                <datalist id="editPageTamanhosList">
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
                <label htmlFor="estadoConservacao"><b>Estado de Conservação</b></label>
                <select
                  id="estadoConservacao"
                  name="estadoConservacao"
                  value={form.estadoConservacao}
                  onChange={handleChange}
                  className={styles.formInput}
                >
                  <option value="">Selecione o estado...</option>
                  <option value="Novo">Novo</option>
                  <option value="Seminovo">Seminovo</option>
                  <option value="Usado - Bom estado">Usado - Bom estado</option>
                  <option value="Usado - Regular">Usado - Regular</option>
                </select>
              </div>

              <div className={styles.formGroup} style={{ flex: 1 }}>
                <label htmlFor="genero"><b>Gênero / Destinação</b></label>
                <select
                  id="genero"
                  name="genero"
                  value={form.genero}
                  onChange={handleChange}
                  className={styles.formInput}
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
                <label htmlFor="tagCode"><b>Código / Tag (Opcional)</b></label>
                <input 
                  id="tagCode" 
                  name="tagCode" 
                  className={styles.formInput}
                  value={form.tagCode} 
                  onChange={handleChange} 
                  placeholder="Ex: PROD-ABC-123" 
                />
              </div>

              <div className={styles.formGroup} style={{ flex: 1 }}>
                <label htmlFor="quantidade"><b>Quantidade *</b></label>
                <input 
                  id="quantidade" 
                  name="quantidade" 
                  type="number" 
                  min={0}
                  className={styles.formInput}
                  value={form.quantidade} 
                  onChange={handleChange} 
                  required 
                  placeholder="Quantidade" 
                />
              </div>
            </div>
            
            <div className={styles.modalButtonGroup} style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', marginTop: '24px' }}>
              <button 
                type="button" 
                className={styles.cancelButton} 
                onClick={() => router.push("/estoque")}
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
            {error && <div className={styles.errorMessage} style={{ marginTop: '16px' }}>{error}</div>}
          </form>
        </div>
      </div>
    </div>
  );
};

export default EditarItem;
