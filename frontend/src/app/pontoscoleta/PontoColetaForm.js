"use client";
import React, { useState } from "react";
import dynamic from "next/dynamic";
import styles from "./pontoscoleta.module.css";

const CollectionPointsMap = dynamic(
  () => import("../components/map/CollectionPointsMap"),
  {
    ssr: false,
    loading: () => <div className={styles.mapPlaceholder}>Carregando mapa...</div>,
  }
);

const EMPTY_FORM = {
  name: "",
  type: "PARTNER_COLLECTION_POINT",
  street: "",
  number: "",
  neighborhood: "",
  city: "Medianeira",
  state: "PR",
  zipCode: "",
  latitude: "",
  longitude: "",
  operatingHours: "",
  phone: "",
};

export default function PontoColetaForm({
  initialData = null,
  onSubmit,
  loading = false,
  submitLabel = "Salvar",
}) {
  const [form, setForm] = useState(() => ({ ...EMPTY_FORM, ...(initialData || {}) }));
  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleMapClick = (lat, lng) => {
    setForm((prev) => ({
      ...prev,
      latitude: Number(lat.toFixed(6)),
      longitude: Number(lng.toFixed(6)),
    }));
  };

  const validate = () => {
    const newErrors = {};
    if (!form.name || form.name.trim().length < 3) newErrors.name = "Nome deve ter ao menos 3 caracteres";
    if (!form.type) newErrors.type = "Tipo é obrigatório";
    if (!form.street || !form.street.trim()) newErrors.street = "Logradouro é obrigatório";

    const lat = parseFloat(form.latitude);
    const lng = parseFloat(form.longitude);
    if (Number.isNaN(lat) || lat < -90 || lat > 90) newErrors.latitude = "Latitude inválida (-90 a 90)";
    if (Number.isNaN(lng) || lng < -180 || lng > 180) newErrors.longitude = "Longitude inválida (-180 a 180)";

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return;

    const payload = {
      ...form,
      latitude: parseFloat(form.latitude),
      longitude: parseFloat(form.longitude),
    };
    onSubmit(payload);
  };

  const selectedPosition =
    form.latitude !== "" && form.longitude !== "" && !Number.isNaN(parseFloat(form.latitude)) && !Number.isNaN(parseFloat(form.longitude))
      ? { lat: parseFloat(form.latitude), lng: parseFloat(form.longitude) }
      : null;

  const mapCenter = selectedPosition ? [selectedPosition.lat, selectedPosition.lng] : undefined;

  return (
    <form onSubmit={handleSubmit} className={styles.formulario}>
      <div className={styles.formGroupFullWidth}>
        <label htmlFor="name"><b>Nome*</b></label>
        <input id="name" name="name" value={form.name} onChange={handleChange}
          placeholder="Ex.: Sede SANEM" className={errors.name ? styles.inputError : ""} />
        {errors.name && <span className={styles.fieldError}>{errors.name}</span>}
      </div>

      <div className={styles.formGroup}>
        <label htmlFor="type"><b>Tipo*</b></label>
        <select id="type" name="type" value={form.type} onChange={handleChange}
          className={errors.type ? styles.inputError : ""}>
          <option value="PARTNER_COLLECTION_POINT">Ponto de coleta parceiro</option>
          <option value="ASSISTANCE_ENTITY_HQ">Sede de entidade assistencial</option>
        </select>
        {errors.type && <span className={styles.fieldError}>{errors.type}</span>}
      </div>

      <div className={styles.formGroup}>
        <label htmlFor="operatingHours"><b>Horário de funcionamento</b></label>
        <input id="operatingHours" name="operatingHours" value={form.operatingHours} onChange={handleChange}
          placeholder="Ex.: Seg-Sex 08:00-18:00" />
      </div>

      <div className={styles.formGroup}>
        <label htmlFor="street"><b>Logradouro*</b></label>
        <input id="street" name="street" value={form.street} onChange={handleChange}
          placeholder="Avenida Brasil" className={errors.street ? styles.inputError : ""} />
        {errors.street && <span className={styles.fieldError}>{errors.street}</span>}
      </div>

      <div className={styles.formGroup}>
        <label htmlFor="number"><b>Número</b></label>
        <input id="number" name="number" value={form.number} onChange={handleChange} placeholder="1500" />
      </div>

      <div className={styles.formGroup}>
        <label htmlFor="neighborhood"><b>Bairro</b></label>
        <input id="neighborhood" name="neighborhood" value={form.neighborhood} onChange={handleChange} placeholder="Centro" />
      </div>

      <div className={styles.formGroup}>
        <label htmlFor="zipCode"><b>CEP</b></label>
        <input id="zipCode" name="zipCode" value={form.zipCode} onChange={handleChange} placeholder="85884-000" />
      </div>

      <div className={styles.formGroup}>
        <label htmlFor="city"><b>Cidade</b></label>
        <input id="city" name="city" value={form.city} onChange={handleChange} placeholder="Medianeira" />
      </div>

      <div className={styles.formGroup}>
        <label htmlFor="state"><b>Estado (UF)</b></label>
        <input id="state" name="state" value={form.state} onChange={handleChange} placeholder="PR" maxLength={2} />
      </div>

      <div className={styles.formGroup}>
        <label htmlFor="phone"><b>Telefone</b></label>
        <input id="phone" name="phone" value={form.phone} onChange={handleChange} placeholder="4535550001" />
      </div>

      <div className={styles.mapHelp}>
        Clique no mapa para definir a localização (latitude/longitude) do ponto, ou preencha os campos abaixo manualmente.
      </div>

      <div className={styles.mapWrapper}>
        <CollectionPointsMap
          points={[]}
          onMapClick={handleMapClick}
          selectedPosition={selectedPosition}
          center={mapCenter}
          height="360px"
        />
      </div>

      <div className={styles.formGroup}>
        <label htmlFor="latitude"><b>Latitude*</b></label>
        <input id="latitude" name="latitude" type="number" step="any" value={form.latitude} onChange={handleChange}
          placeholder="-25.2955" className={errors.latitude ? styles.inputError : ""} />
        {errors.latitude && <span className={styles.fieldError}>{errors.latitude}</span>}
      </div>

      <div className={styles.formGroup}>
        <label htmlFor="longitude"><b>Longitude*</b></label>
        <input id="longitude" name="longitude" type="number" step="any" value={form.longitude} onChange={handleChange}
          placeholder="-54.0936" className={errors.longitude ? styles.inputError : ""} />
        {errors.longitude && <span className={styles.fieldError}>{errors.longitude}</span>}
      </div>

      <div className={styles.actions}>
        <button type="submit" className={styles.submitButton} disabled={loading}>
          {loading ? "Salvando..." : submitLabel}
        </button>
      </div>
    </form>
  );
}
